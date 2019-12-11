package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * 根据key和参数启动流程定义
     * @param processDefinitionKey  流程定义key， 必填
     * @param businessKey   业务key，选填
     * @param variables     启动变量，选填
     * @param request
     * @return 流程实例对象封装
     */
    @RequestMapping(value = "/startProcessInstanceByKey", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@RequestParam String processDefinitionKey, @RequestParam(required = false) String businessKey, @RequestParam String userId, @RequestParam(required = false) Map<String, Object> variables, HttpServletRequest request) throws Exception {
        //流程发起前设置发起人，记录在流程历史中
//        在流程开始之前设置，会自动在表ACT_HI_PROCINST 中的START_USER_ID_中设置用户ID：
//        用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        return BaseOutput.success().setData(DTOUtils.as(processInstance, ProcessInstanceMapping.class));
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
        return BaseOutput.success().setData(DTOUtils.as(processInstance, ProcessInstanceMapping.class));
    }

    /**
     * 查看进度图片
     * @param processDefinitionId
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/progress", method = {RequestMethod.GET})
    public void showImageByProcessInstanceId(@RequestParam String processDefinitionId, @RequestParam String processInstanceId, HttpServletResponse response) throws Exception{
        activitiService.processTracking(processDefinitionId, processInstanceId, response.getOutputStream());
    }

}
