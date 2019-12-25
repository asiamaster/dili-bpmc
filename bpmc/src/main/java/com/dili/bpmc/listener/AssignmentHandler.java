package com.dili.bpmc.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.bpmc.domain.TaskAssignment;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.bpmc.service.TaskAssignmentService;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.BeanConver;
import com.dili.ss.util.DateUtils;
import okhttp3.Response;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务办理人处理器
 */
@Component
public class AssignmentHandler implements TaskListener {

    public final static Logger log = LoggerFactory.getLogger(AssignmentHandler.class);
    @Autowired
    private TaskAssignmentService taskAssignmentService;
    @Autowired
    private RepositoryService repositoryService;
    @Override
    public void notify(DelegateTask delegateTask) {
        String taskDefKey = delegateTask.getTaskDefinitionKey();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(delegateTask.getProcessDefinitionId()).singleResult();
        //流程定义可能不存在
        if(processDefinition == null){
            return;
        }
        String procDefKey = processDefinition.getKey();
        //根据任务定义和流程定义查询任务分配方式
        TaskAssignment condition = DTOUtils.newInstance(TaskAssignment.class);
        condition.setTaskDefinitionKey(taskDefKey);
        condition.setProcessDefinitionKey(procDefKey);
        List<TaskAssignment> taskAssignmentList = taskAssignmentService.list(condition);
        if(CollectionUtils.isEmpty(taskAssignmentList)){
            return;
        }
        TaskAssignment taskAssignment = taskAssignmentList.get(0);
        //优先处理URL的方式
        if(StringUtils.isNotBlank(taskAssignment.getHandlerUrl())){
            //构建参数
            TaskMapping taskMapping = DTOUtils.asInstance(delegateTask, TaskMapping.class);
            taskMapping.setProcessVariables(delegateTask.getVariables());
            Map<String, Object> params = new HashMap();
            try {
                params = BeanConver.transformObjectToMap(taskMapping);
                Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Object> entry = it.next();
                    if(entry.getValue() instanceof Date){
                        entry.setValue(DateUtils.format((Date) entry.getValue()));
                    }else if(entry.getKey().equalsIgnoreCase("processVariables")){
                        entry.setValue(JSON.toJSONString(entry.getValue(), SerializerFeature.IgnoreErrorGetter));
                    }else if(entry.getKey().equalsIgnoreCase("fields")){
                        it.remove();
                    }else{
                        entry.setValue(entry.getValue().toString());
                    }
                }
            } catch (Exception e) {
            }
            Assignment assignment = remoteHandle(taskAssignment.getHandlerUrl(), (Map)params);
            //可能远程处理没有结果或报错
            if(assignment == null){
                return;
            }
            if(StringUtils.isNotBlank(assignment.getAssignee())) {
                delegateTask.setAssignee(assignment.getAssignee());
            }
            if(CollectionUtils.isNotEmpty(assignment.getCandidateUser())) {
                delegateTask.addCandidateUsers(assignment.getCandidateUser());
            }
            if(CollectionUtils.isNotEmpty(assignment.getCandidateGroup())) {
                delegateTask.addCandidateGroups(assignment.getCandidateGroup());
            }
        }else{//没有URL，则直接取TaskAssignment中配置的分配方式
            if(StringUtils.isNotBlank(taskAssignment.getAssignee())) {
                delegateTask.setAssignee(taskAssignment.getAssignee());
            }
            if(StringUtils.isNotBlank(taskAssignment.getCandidateUser())) {
                delegateTask.addCandidateUsers(Arrays.asList(taskAssignment.getCandidateUser().split(",")));
            }
            if(StringUtils.isNotBlank(taskAssignment.getCandidateGroup())) {
                delegateTask.addCandidateGroups(Arrays.asList(taskAssignment.getCandidateGroup().split(",")));
            }
        }
    }

    /**
     *
     * @return
     */
    private Assignment remoteHandle(String url, Map<String, String> params){
        //默认重试三次
        int retryInt = 3;
        //默认三秒重试
        long intervalLong = 3000;
        while(retryInt > 0) {
            try {
                Response resp = OkHttpUtils.post().url(url).params(params).build()
                        //30秒过期
                        .connTimeOut(1000L * 30L).readTimeOut(1000L * 30L).writeTimeOut(1000L * 30L).execute();
                if (resp.isSuccessful()) {
                    String result = resp.body().string();
                    BaseOutput baseOutput = JSON.parseObject(result, BaseOutput.class, Feature.IgnoreNotMatch);
                    if (baseOutput.isSuccess()) {
                        JSONObject jsonObject = (JSONObject)baseOutput.getData();
                        return DTOUtils.asInstance(jsonObject, Assignment.class);
                    } else {
                        retryInt--;
                        log.error(String.format("远程调用[" + url + "]内部异常,message:[%s]", baseOutput.getMessage()));
                        Thread.sleep(intervalLong);
                    }
                } else {
                    retryInt--;
                    log.error(String.format("远程调用[" + url + "]发生失败,%s秒后重试,code:[%s], message:[%s]", intervalLong/1000L, resp.code(), resp.message()));
                    Thread.sleep(intervalLong);
                }
            } catch (IOException | InterruptedException e) {
                log.error(String.format("远程调用[" + url + "]发生异常,message:[%s],%s秒后重试", e.getMessage(), intervalLong/1000L));
                retryInt--;
                try {
                    Thread.sleep(intervalLong);
                } catch (InterruptedException ex) {
                    //dont care
                }
            }
        }
        return null;
    }
}
