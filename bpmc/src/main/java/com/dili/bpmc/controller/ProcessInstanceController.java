package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.ss.activiti.component.CustomProcessDiagramGenerator;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.IBaseDomain;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.session.SessionContext;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.collections4.CollectionUtils;
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
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 流程实例controller
 * @author asiamaster
 * @date 2019-3-6
 * @since 1.0
 */
@Controller
@RequestMapping("/pi")
public class ProcessInstanceController {
    private final Logger log = LoggerFactory.getLogger(ProcessInstanceController.class);
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActivitiService activitiService;

    private final String INDEX = "/pi/index.html";

    /**
     * 根据流程定义和流程实例id显示图片
     * @param processDefinitionId 如果流程定义id不存在也可以显示，但性能较差
     * @param processInstanceId
     * @param model
     * @return
     */
    @RequestMapping(value = "/showImage.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String showImage(@RequestParam String processDefinitionId, @RequestParam String processInstanceId,ModelMap model) {
    	model.addAttribute("processDefinitionId", processDefinitionId);
    	model.addAttribute("processInstanceId", processInstanceId);
    	return "process/showImage";
    }

    /**
     * 我发起的流程
     * @param procInstId 流程实例id
     * @param request
     * @return
     */
    @RequestMapping(value = "/myProcInst.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String myProcInst(@RequestParam(required = false) String procInstId, HttpServletRequest request) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            throw new NotLoginException();
        }
        //查询当前用户作为流程发起人的流程
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().involvedUser(userTicket.getId().toString()).list();
        //我发起的流程实例数
        request.setAttribute("procInstCount", historicProcessInstances.size());
        //我发起的流程实例
        request.setAttribute("procInsts", JSONArray.toJSONString(historicProcessInstances, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter));
        if(!historicProcessInstances.isEmpty()) {
            //当前显示的流程实例
            request.setAttribute("procInst", historicProcessInstances.get(0));
        }
        //进行中的任务
        List<Task> runningTasks = taskService.createTaskQuery().processInstanceId(procInstId).active().list();
        if(!CollectionUtils.isEmpty(runningTasks)) {
            request.setAttribute("runningTasks", runningTasks);
        }

        return "process/myProcInst";
    }
    
    /**
     * 流程实例管理首页
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping(value = "/index.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(IBaseDomain param, ModelMap modelMap, HttpServletRequest request) throws Exception {
//        List<ProcessInstanceDto> processInstances = runtimeService.createProcessInstanceQuery().active().list();
//        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput();
//        easyuiPageOutput.setRows(runtimeService.createProcessInstanceQuery().active().listPage(0, 10));
//        Long total = runtimeService.createProcessInstanceQuery().active().count();
//        easyuiPageOutput.setTotal(total.intValue());
//        request.setAttribute("processInstances", JSON.toJSONString(easyuiPageOutput, SerializerFeature.IgnoreErrorGetter));
        // 查询流程实例
//        long count = runtimeService.createProcessInstanceQuery().count();
//        System.out.println("流程实例数量：" + count);
        return "process/pi";
    }

    /**
     * 查询流程实例列表
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(IBaseDomain param) throws Exception {
        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput();
        int firstResult = (param.getPage()-1)*param.getRows();
        easyuiPageOutput.setRows(runtimeService.createProcessInstanceQuery().active().listPage(firstResult, param.getRows()));
        Long total = runtimeService.createProcessInstanceQuery().active().count();
        easyuiPageOutput.setTotal(total.intValue());
        return JSON.toJSONString(easyuiPageOutput, SerializerFeature.IgnoreErrorGetter);
    }

    /**
     * 结束流程定义
     * @param processInstanceId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/end.action", method = {RequestMethod.GET, RequestMethod.POST})
    public void end(@RequestParam String processInstanceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //流程已经结束
        //顺序不能换
        if (activitiService.isFinished2(processInstanceId)) {
            historyService.deleteHistoricProcessInstance(processInstanceId);
            runtimeService.suspendProcessInstanceById(processInstanceId);
        } else {//流程没有结束
//            taskService.addComment(taskId, processInstanceId, comment);//备注
            runtimeService.deleteProcessInstance(processInstanceId, "");
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 将流程实例下的所有任务
     * 流转到下一节点
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/process.action", method = {RequestMethod.GET})
    public void process(@RequestParam String processInstanceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
        if(!tasks.isEmpty()) {
            for (Task task : tasks) {
                taskService.claim(task.getId(), task.getAssignee());
                taskService.complete(task.getId());
//                formService.submitTaskFormData(task.getId(), new HashMap<>());
            }
        }
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 运行到结束
     * @param processDefinitionId
     * @return
     */
    @RequestMapping(value = "/run.action", method = {RequestMethod.GET})
    public void run(@RequestParam String processDefinitionId, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        ProcessInstanceDto processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        List<Task> tasks = taskService.createTaskQuery().active().list();
        while(!tasks.isEmpty()) {
            for (Task task : tasks) {
                taskService.claim(task.getId(), task.getAssignee());
                taskService.complete(task.getId());
//                formService.submitTaskFormData(task.getId(), new HashMap<>());
            }
            tasks = taskService.createTaskQuery().active().list();
        }
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 查看进度图片
     * @param processDefinitionId
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/progress.action", method = {RequestMethod.GET})
    public void showImageByProcessInstanceId(@RequestParam String processDefinitionId, @RequestParam String processInstanceId, HttpServletResponse response) throws Exception{
        activitiService.processTracking(processDefinitionId, processInstanceId, response.getOutputStream());
    }

}
