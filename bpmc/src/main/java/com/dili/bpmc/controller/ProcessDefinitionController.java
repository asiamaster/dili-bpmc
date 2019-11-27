package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.session.SessionContext;
import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 流程定义控制器
 * @Author: WangMi
 * @Date: 2019/11/15 10:59
 * @Description:
 */
@Controller
@RequestMapping("/pd")
public class ProcessDefinitionController {

    private final Logger log = LoggerFactory.getLogger(ProcessDefinitionController.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private FormService formService;
    private final String INDEX = "/pd/index.html";

    /**
     * 流程实例管理首页
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping(value = "/index.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(ModelMap modelMap, HttpServletRequest request) throws Exception {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        request.setAttribute("processDefinitions", JSONArray.toJSONString(processDefinitions, SerializerFeature.IgnoreErrorGetter));
        return "process/pd";
    }

    /**
     * 根据key和参数启动流程定义
     * @param key
     * @param businessKey
     * @param variables
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/startProcessInstanceByKey.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> startProcessInstanceByKey(@RequestParam String key, @RequestParam(required = false) String businessKey, @RequestParam(required = false) Map<String, Object> variables, HttpServletRequest request) throws IOException {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, businessKey, variables);
        return BaseOutput.success().setData(processInstance.getId());
    }

    /**
     * 启动流程定义
     * @param processDefinitionId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/start.action", method = {RequestMethod.GET, RequestMethod.POST})
    public void start(@RequestParam String processDefinitionId, @RequestParam(required = false) String businessKey, @RequestParam(required = false) Map<String, Object> variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            throw new NotLoginException();
        }
        //流程发起前设置发起人，记录在流程历史中
        identityService.setAuthenticatedUserId(userTicket.getId().toString());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
//        ProcessInstanceDto processInstance = formService.submitStartFormData(processDefinitionId);
//        根据流程发起人查询流程定义id
//        System.out.println(historyService.createHistoricProcessInstanceQuery()
//                .startedBy("1").list().get(0).getProcessDefinitionId());
//        根据流程实例id获取发起人
//        System.out.println(historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult().getStartUserId());;
//        runtimeService.startProcessInstanceByKey("interview", "businessKey1");
        // 查询流程实例
//        long count = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).count();
//        System.out.println(processDefinitionId + "流程实例数量：" + count);
//        List<TaskDto> tasks = taskService.createTaskQuery().taskAssignee("47").list();
        response.sendRedirect(request.getContextPath() + INDEX );
    }

    /**
     * 删除流程定义
     * @param deploymentId  部署id
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public void delete(@RequestParam String deploymentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        repositoryService.deleteDeployment(deploymentId);
        response.sendRedirect(request.getContextPath() + INDEX );
    }

    /**
     * 删除流程定义
     * @param deploymentId  部署id
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/clear.action", method = {RequestMethod.GET, RequestMethod.POST})
    public void clear(@RequestParam String deploymentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //将给定的部署和级联删除删除到流程实例、历史流程实例和作业。
        repositoryService.deleteDeployment(deploymentId, true);
        response.sendRedirect(request.getContextPath() + INDEX );
    }

    /**
     * 开始节点表单页面
     * @param processDefinitionId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/startForm.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String startForm(@RequestParam String processDefinitionId, HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) throws IOException {
        String formKey = formService.getStartFormKey(processDefinitionId);
        if(StringUtils.isBlank(formKey)){
            throw new RuntimeException("流程定义["+processDefinitionId+"]没有开始表单");
        }
        attr.addAttribute("formKey", formKey);
        attr.addAttribute("processDefinitionId", processDefinitionId);
        return "redirect:"+request.getContextPath()+"/actControl/dynamicForm.html";
    }

    /**
     * 获取开始节点表单信息
     * @param processDefinitionId
     * @param request
     * @param response
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/startFormData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public String startFormData(@RequestParam String processDefinitionId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        StartFormData startFormData =  formService.getStartFormData(processDefinitionId);
        List<FormProperty> formProperties = startFormData.getFormProperties();
        return formProperties.toString();
    }
}
