package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.ExecutionMapping;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.HistoricProcessInstanceQueryDto;
import com.dili.bpmc.sdk.dto.ProcessInstanceVariablesDto;
import com.dili.bpmc.sdk.dto.StartProcessInstanceDto;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运行时接口
 * @author asiamaster
 * @date 2019-11-29
 * @since 1.0
 */
@RestController
@RequestMapping("/api/runtime")
public class RuntimeApi {
    private final Logger log = LoggerFactory.getLogger(RuntimeApi.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private ActivitiService activitiService;

    /**
     * 根据实例id查询进行中的执行
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/listExecution", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<ExecutionMapping>> listExecution(@RequestParam String processInstanceId){
        List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        return BaseOutput.successData(DTOUtils.as(executionList, ExecutionMapping.class));
    }

    /**
     * 获取流程变量
     * @param processInstanceId
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/getVariables", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<Map<String, Object>> getVariables(@RequestParam String processInstanceId, @RequestParam(required = false) String activityId){
        try {
            // 当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值
            ExecutionQuery executionQuery = runtimeService.createExecutionQuery().processInstanceId(processInstanceId);
            if(StringUtils.isNotBlank(activityId)){
                executionQuery.activityId(activityId);
            }
            List<Execution> list = executionQuery.list();
            if (CollectionUtils.isEmpty(list)) {
                return BaseOutput.failure("执行["+processInstanceId + "." + activityId+"]不存在");
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            list.forEach(t -> {
                if(t.getActivityId() == null){
                    return;
                }
                Map<String, Object> variables = runtimeService.getVariables(t.getId());
                stringObjectHashMap.putAll(variables);
            });
            return BaseOutput.successData(stringObjectHashMap);
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 根据实例id和活动id获取唯一的执行id，设置流程变量
     *
     * @param setProcessInstanceVariablesDto processInstanceId
     * @param setProcessInstanceVariablesDto activityId
     * @param setProcessInstanceVariablesDto variables
     * @return
     */
    @RequestMapping(value = "/setVariables", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput setVariables(ProcessInstanceVariablesDto setProcessInstanceVariablesDto){
        try {
            // 当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(setProcessInstanceVariablesDto.getProcessInstanceId()).activityId(setProcessInstanceVariablesDto.getActivityId())
                    .singleResult();
            if (execution == null) {
                return BaseOutput.failure("执行["+setProcessInstanceVariablesDto.getProcessInstanceId() + "." + setProcessInstanceVariablesDto.getActivityId()+"]不存在");
            }
            runtimeService.setVariables(execution.getId(), setProcessInstanceVariablesDto.getVariables());
            return BaseOutput.success();
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 删除流程变量
     * @param processInstanceId
     * @param activityId
     * @param key
     * @return
     */
    @RequestMapping(value = "/removeVariable", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput removeVariable(@RequestParam String processInstanceId, @RequestParam String activityId, @RequestParam String key){
        try {
            // 当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).activityId(activityId)
                    .singleResult();
            if (execution == null) {
                return BaseOutput.failure("执行["+processInstanceId + "." + activityId+"]不存在");
            }
            runtimeService.removeVariable(execution.getId(), key);
            return BaseOutput.success();
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 设置流程变量
     * @param processInstanceId
     * @param activityId
     * @param key
     * @param value
     * @return
     */
    @RequestMapping(value = "/setVariable", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput setVariable(@RequestParam String processInstanceId, @RequestParam String activityId, @RequestParam String key, @RequestParam String value){
        try {
            // 当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).activityId(activityId)
                    .singleResult();
            if (execution == null) {
                return BaseOutput.failure("执行["+processInstanceId + "." + activityId+"]不存在");
            }
            runtimeService.setVariable(execution.getId(), key, value);
            return BaseOutput.success();
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

//    @RequestMapping(value = "/setVariables", method = {RequestMethod.GET, RequestMethod.POST})
//    public BaseOutput setVariables(@RequestParam String executionId, @RequestParam Map<String, String> variables){
//        try {
//            runtimeService.setVariables(executionId, variables);
//            return BaseOutput.success();
//        } catch (Exception e) {
//            return BaseOutput.failure(e.getMessage());
//        }
//    }
//    @RequestMapping(value = "/setVariable", method = {RequestMethod.GET, RequestMethod.POST})
//    public BaseOutput setVariables(@RequestParam String executionId, @RequestParam String key, @RequestParam String value){
//        try {
//            runtimeService.setVariable(executionId, key, value);
//            return BaseOutput.success();
//        } catch (Exception e) {
//            return BaseOutput.failure(e.getMessage());
//        }
//    }

    /**
     * 根据流程实例id或businessKey查询进行中的流程实例
     * 两个参数至少传一个
     * @param processInstanceId
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "/findActiveProcessInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> findActiveProcessInstance(@RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String businessKey, @RequestParam(required = false) String superProcessInstanceId){
        if(StringUtils.isBlank(processInstanceId) && StringUtils.isBlank(businessKey) && StringUtils.isBlank(superProcessInstanceId)){
            return BaseOutput.failure("superProcessInstanceId、processInstanceId或businessKey不能全部为空");
        }
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if(StringUtils.isNotBlank(processInstanceId)){
            processInstanceQuery.processInstanceId(processInstanceId);
        }
        if(StringUtils.isNotBlank(businessKey)){
            processInstanceQuery.processInstanceBusinessKey(businessKey);
        }
        if(StringUtils.isNotBlank(superProcessInstanceId)){
            processInstanceQuery.superProcessInstanceId(superProcessInstanceId);
        }
        try {
            ProcessInstance processInstance = processInstanceQuery.singleResult();
            if(processInstance == null){
                return BaseOutput.success("未查询到流程实例");
            }
            return BaseOutput.successData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 根据流程实例id或businessKey查询流程实例
     * 两个参数至少传一个
     * @param processInstanceId
     * @param businessKey
     * @param superProcessInstanceId 父流程实例id
     * @return
     */
    @RequestMapping(value = "/findHistoricProcessInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> findHistoricProcessInstance(@RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String businessKey, @RequestParam(required = false) String superProcessInstanceId){
        if(StringUtils.isBlank(processInstanceId) && StringUtils.isBlank(businessKey)){
            return BaseOutput.failure("processInstanceId或businessKey不能为空");
        }
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.isNotBlank(processInstanceId)){
            historicProcessInstanceQuery.processInstanceId(processInstanceId);
        }
        if(StringUtils.isNotBlank(businessKey)){
            historicProcessInstanceQuery.processInstanceBusinessKey(businessKey);
        }
        if(StringUtils.isNotBlank(superProcessInstanceId)){
            historicProcessInstanceQuery.superProcessInstanceId(superProcessInstanceId);
        }
        try {
            HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.singleResult();
            if(historicProcessInstance == null){
                return BaseOutput.success("未查询到流程实例");
            }
            return BaseOutput.successData(DTOUtils.asInstance(historicProcessInstance, ProcessInstanceMapping.class));
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 查询历史流程实例列表
     * @param historicProcessInstanceQueryDto
     * @return
     */
    @RequestMapping(value = "/listHistoricProcessInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<ProcessInstanceMapping>> listHistoricProcessInstance(HistoricProcessInstanceQueryDto historicProcessInstanceQueryDto){
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.isNotBlank(historicProcessInstanceQueryDto.getProcessInstanceId())){
            historicProcessInstanceQuery.processInstanceId(historicProcessInstanceQueryDto.getProcessInstanceId());
        }
        if(StringUtils.isNotBlank(historicProcessInstanceQueryDto.getProcessDefinitionKey())){
            historicProcessInstanceQuery.processDefinitionKey(historicProcessInstanceQueryDto.getProcessDefinitionKey());
        }
        if(StringUtils.isNotBlank(historicProcessInstanceQueryDto.getProcessDefinitionId())){
            historicProcessInstanceQuery.processDefinitionId(historicProcessInstanceQueryDto.getProcessDefinitionId());
        }
        if(StringUtils.isNotBlank(historicProcessInstanceQueryDto.getBusinessKey())){
            historicProcessInstanceQuery.processInstanceBusinessKey(historicProcessInstanceQueryDto.getBusinessKey());
        }
        if(StringUtils.isNotBlank(historicProcessInstanceQueryDto.getSuperProcessInstanceId())){
            historicProcessInstanceQuery.superProcessInstanceId(historicProcessInstanceQueryDto.getSuperProcessInstanceId());
        }
        if(historicProcessInstanceQueryDto.getFinished() != null){
            if(historicProcessInstanceQueryDto.getFinished()){
                historicProcessInstanceQuery.finished();
            }else{
                historicProcessInstanceQuery.unfinished();
            }
        }
        return BaseOutput.successData(DTOUtils.asInstance(historicProcessInstanceQuery.list(), ProcessInstanceMapping.class));
    }

    /**
     * 根据流程实例id更新businessKey
     * @param processInstanceId
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "/updateBusinessKey", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput updateBusinessKey(@RequestParam String processInstanceId, @RequestParam String businessKey){
        runtimeService.updateBusinessKey(processInstanceId, businessKey);
        return BaseOutput.success();
    }

    /**
     * 根据key和参数启动流程定义
     * @param startProcessInstanceDto processDefinitionKey  流程定义key， 必填
     * @param startProcessInstanceDto businessKey   业务key，选填
     * @param startProcessInstanceDto userId   用户id，必填
     * @param startProcessInstanceDto variables     启动变量，选填
     * @return 流程实例对象封装
     */
    @RequestMapping(value = "/startProcessInstanceByKey", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(StartProcessInstanceDto startProcessInstanceDto) throws Exception {
        //流程发起前设置发起人，记录在流程历史中
//        在流程开始之前设置，会自动在表ACT_HI_PROCINST 中的START_USER_ID_中设置用户ID：
//        用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(startProcessInstanceDto.getUserId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startProcessInstanceDto.getProcessDefinitionKey(), startProcessInstanceDto.getBusinessKey(), startProcessInstanceDto.getVariables());
        return BaseOutput.success().setData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
    }

    /**
     * 根据流程定义id和参数启动流程定义
     * @param startProcessInstanceDto processDefinitionId 流程定义id, 必填
     * @param startProcessInstanceDto businessKey   业务key，选填
     * @param startProcessInstanceDto userId   用户id，必填
     * @param startProcessInstanceDto variables     启动变量，选填
     * @return 流程实例对象封装
     */
    @RequestMapping(value = "/startProcessInstanceById", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> startProcessInstanceById(StartProcessInstanceDto startProcessInstanceDto) throws Exception {
        //流程发起前设置发起人，记录在流程历史中
//        在流程开始之前设置，会自动在表ACT_HI_PROCINST 中的START_USER_ID_中设置用户ID：
//        用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(startProcessInstanceDto.getUserId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(startProcessInstanceDto.getProcessDefinitionId(), startProcessInstanceDto.getBusinessKey(), startProcessInstanceDto.getVariables());
        return BaseOutput.success().setData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
    }

    /**
     * 结束进行中的流程实例
     * @param processInstanceId
     * @param deleteReason  结束原因
     * @throws IOException
     */
    @RequestMapping(value = "/stopProcessInstanceById", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput stopProcessInstanceById(@RequestParam String processInstanceId, @RequestParam(required = false) String deleteReason) throws IOException {
        try {
            //流程已经结束
            //顺序不能换
            if (activitiService.isFinished2(processInstanceId)) {
                historyService.deleteHistoricProcessInstance(processInstanceId);
//                runtimeService.suspendProcessInstanceById(processInstanceId);
            } else {//流程没有结束
//            taskService.addComment(taskId, processInstanceId, comment);//备注
                runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
                historyService.deleteHistoricProcessInstance(processInstanceId);
            }
            return BaseOutput.success("流程已结束");
        }catch (Exception e){
            return BaseOutput.failure("流程结束失败:"+e.getMessage());
        }
    }

    /**
     * 查看进度图片
     * 缺少流程定义id性能较差，并且无法查看已完成的流程
     * @param processDefinitionId 流程定义id
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/progress", method = {RequestMethod.GET})
    public void showImageByProcessInstanceId(@RequestParam(required = false) String processDefinitionId, @RequestParam String processInstanceId, HttpServletResponse response) throws Exception{
        activitiService.processTracking(processDefinitionId, processInstanceId, response.getOutputStream());
    }

}
