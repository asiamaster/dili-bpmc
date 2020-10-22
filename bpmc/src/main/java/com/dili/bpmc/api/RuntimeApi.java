package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.HistoricProcessInstanceQueryDto;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * 根据流程实例id或businessKey查询进行中的流程实例
     * 两个参数至少传一个
     * @param processInstanceId
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "/findActiveProcessInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> findActiveProcessInstance(@RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String businessKey, @RequestParam(required = false) String superProcessInstanceId){
        if(StringUtils.isBlank(processInstanceId) && StringUtils.isBlank(businessKey)){
            return BaseOutput.failure("processInstanceId或businessKey不能为空");
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
        ProcessInstance processInstance = processInstanceQuery.singleResult();
        if(processInstance == null){
            return BaseOutput.success("未查询到流程实例");
        }
        return BaseOutput.successData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
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
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.singleResult();
        if(historicProcessInstance == null){
            return BaseOutput.success("未查询到流程实例");
        }
        return BaseOutput.successData(DTOUtils.asInstance(historicProcessInstance, ProcessInstanceMapping.class));
    }

    /**
     * 查询历史流程实例列表
     * @param historicProcessInstanceQueryDto
     * @return
     */
    @RequestMapping(value = "/listHistoricProcessInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<ProcessInstanceMapping>> listProcessInstance(HistoricProcessInstanceQueryDto historicProcessInstanceQueryDto){
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
     * @param processDefinitionKey  流程定义key， 必填
     * @param businessKey   业务key，选填
     * @param variables     启动变量，选填
     * @param request
     * @return 流程实例对象封装
     */
    @RequestMapping(value = "/startProcessInstanceByKey", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@RequestParam String processDefinitionKey, @RequestParam(required = false) String businessKey, @RequestParam String userId, @RequestParam(required = false) Map<String, Object> variables, HttpServletRequest request) throws Exception {
        //流程发起前设置发起人，记录在流程历史中
//        在流程开始之前设置，会自动在表ACT_HI_PROCINST 中的START_USER_ID_中设置用户ID：
//        用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        return BaseOutput.success().setData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
    }

    /**
     * 根据流程定义id和参数启动流程定义
     * @param processDefinitionId 流程定义id
     * @param businessKey   业务key，选填
     * @param variables     启动变量，选填
     * @return 流程实例对象封装
     */
    @RequestMapping(value = "/startProcessInstanceById", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessInstanceMapping> startProcessInstanceById(@RequestParam String processDefinitionId, @RequestParam(required = false) String businessKey, @RequestParam String userId, @RequestParam(required = false) Map<String, Object> variables, HttpServletRequest request) throws Exception {
        //流程发起前设置发起人，记录在流程历史中
//        在流程开始之前设置，会自动在表ACT_HI_PROCINST 中的START_USER_ID_中设置用户ID：
//        用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
        return BaseOutput.success().setData(DTOUtils.asInstance(processInstance, ProcessInstanceMapping.class));
    }

    /**
     * 结束流程实例
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
                runtimeService.suspendProcessInstanceById(processInstanceId);
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
