package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.bpmc.consts.TaskCategory;
import com.dili.bpmc.domain.TaskDto;
import com.dili.bpmc.rpc.RoleRpc;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例controller
 * @author asiamaster
 * @date 2019-3-6
 * @since 1.0
 */
@Controller
@RequestMapping("/task")
public class TaskController {
    private final Logger log = LoggerFactory.getLogger(TaskController.class);
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
    @Autowired
    private RoleRpc roleRpc;
    private final String INDEX = "/task/index.html";

    /**
     * 任务管理首页
     * @param request
     * @return
     */
    @RequestMapping(value = "/index.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(@RequestParam(required = false) String userId,HttpServletRequest request) {
//        TaskQuery taskQuery = taskService.createTaskQuery().active();
//        List<TaskDto> tasks;
//        if(StringUtils.isNotBlank(userId)) {
//            tasks = taskQuery.taskCandidateOrAssigned(userId).list();
//        }else{
//            tasks = taskQuery.list();
//        }
//        request.setAttribute("tasks", JSONArray.toJSONString(tasks, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreErrorGetter));
        return "process/task";
    }

    /**
     * 任务中心页面
     * @param userId 用户ID
     * @param category 页面类型(inbox(默认), tasks, queued, involved和archived)
     * @param group 当category=queued时，group为用户组id
     * @return
     */
    @RequestMapping(value = "/tasks.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String tasks(@RequestParam(required = false) String userId, @RequestParam(defaultValue = "inbox") String category, @RequestParam(required = false) String group,  HttpServletRequest request) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        //如果没有userId参数，则使用当前登录的用户
        if(StringUtils.isBlank(userId)){
            userId = userTicket.getId().toString();
        }
        handleTaskCategory(category, userId, request);
        return "process/task";
    }

    /**
     * 统一处理不同类别的任务
     * @param category
     * @param userId
     */
    private void handleTaskCategory(String category, String userId, HttpServletRequest request){
        TaskQuery taskQuery = taskService.createTaskQuery();
        //待办任务
//        指派给当前登录用户的任务
        long inboxCount = taskQuery.taskAssignee(userId).count();
        //我的任务
//        属主的任务
        long tasksCount = taskQuery.taskOwner(userId).count();
        BaseOutput<List<Role>> rolesOutput = roleRpc.listByUser(Long.valueOf(userId), null);
        if(!rolesOutput.isSuccess()){
            throw new AppException("远程查询角色失败");
        }
        //队列任务
//        候选用户组下的任务
        long queuedCount = 0;
        List<Role> roles = rolesOutput.getData();
        for(Role role : roles) {
            queuedCount += taskQuery.taskCandidateGroup(role.getId().toString()).count();
        }
        //受邀任务，这里和Activiti-explorer不同，只处理候选用户任务
        long involvedCount = taskQuery.taskCandidateUser(userId).count();

        request.setAttribute("inboxCount", inboxCount);
        request.setAttribute("tasksCount", tasksCount);
        request.setAttribute("queuedCount", queuedCount);
        request.setAttribute("involvedCount", involvedCount);
        //设置每个类别的第一个任务，用于在点击时直接跳转到该任务
        //TODO

        //设置当前类别的任务，用于列出该类别下所有任务
        List<Task> tasks = null;
        if(TaskCategory.INBOX.getCode().equals(category)){
            tasks = taskQuery.taskAssignee(userId).list();
        }else if(TaskCategory.TASKS.getCode().equals(category)){
            tasks = taskQuery.taskOwner(userId).list();
        }else if(TaskCategory.QUEUED.getCode().equals(category)){
            tasks = new ArrayList<>();
            for(Role role : roles) {
                tasks.addAll(taskQuery.taskCandidateGroup(role.getId().toString()).list());
            }
        }else if(TaskCategory.INVOLVED.getCode().equals(category)){
            tasks = taskQuery.taskCandidateUser(userId).list();
        }
        request.setAttribute("tasks", tasks);
    }

    /**
     * 查询流程实例列表
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskDto param) throws Exception {
        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput();
        TaskQuery taskQuery = taskService.createTaskQuery();
        if(StringUtils.isNotBlank(param.getAssignee())){
            taskQuery.taskAssignee(param.getAssignee());
        }
        if(StringUtils.isNotBlank(param.getProcessDefinitionId())){
            taskQuery.processDefinitionId(param.getProcessDefinitionId());
        }
        if(StringUtils.isNotBlank(param.getProcessInstanceId())){
            taskQuery.processInstanceId(param.getProcessInstanceId());
        }
        if(StringUtils.isNotBlank(param.getTaskId())){
            taskQuery.taskId(param.getTaskId());
        }
        int firstResult = (param.getPage()-1)*param.getRows();
        List list = taskQuery.listPage(firstResult, param.getRows());
        List results = ValueProviderUtils.buildDataByProvider(param, list);
        easyuiPageOutput.setRows(results);
        Long total = taskQuery.count();
        easyuiPageOutput.setTotal(total.intValue());
        return JSON.toJSONString(easyuiPageOutput, SerializerFeature.IgnoreErrorGetter);
    }

    /**
     * 任务节点表单页面
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param taskId
     * @param request
     * @param response
     * @param attr
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/startForm.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String startForm(@RequestParam String processDefinitionId, @RequestParam String taskDefinitionKey, @RequestParam String taskId, HttpServletRequest request, HttpServletResponse response,RedirectAttributes attr) throws IOException {
        String formKey = formService.getTaskFormKey(processDefinitionId, taskDefinitionKey);
        if(StringUtils.isBlank(formKey)){
            throw new RuntimeException("任务["+taskDefinitionKey+"]表单不存在");
        }
        attr.addAttribute("formKey", formKey);
        attr.addAttribute("taskId", taskId);
        return "redirect:"+request.getContextPath()+"/actControl/dynamicForm.html";
    }

    /**
     * 申领任务
     * @param userId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/claim.action", method = {RequestMethod.GET})
    public void claim(@RequestParam String taskId, @RequestParam String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        TaskDto task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();
        taskService.claim(taskId, userId);
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 根据流程实例id申领任务，取当前登录用户
     * @param processInstanceId
     * @param variables
     * @param request
     * @return
     */
    @RequestMapping(value = "/claimAndCompleteByPi.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput claimAndCompleteByPi(@RequestParam String processInstanceId, @RequestParam Map variables, HttpServletRequest request){
        try {
            String userId = "";
            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
            if (userTicket == null) {
                return BaseOutput.failure("用户未登录");
            }
            userId =  userTicket.getId().toString();
            // 根据流程实例id获取任务
            List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            for(org.activiti.engine.task.Task task : tasks) {
                // 当前用户申领任务
                taskService.claim(task.getId(), userId);
                // 完成任务
                taskService.complete(task.getId(), variables);
            }
            return BaseOutput.success();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 根据taskId申领任务，取当前用户
     * @param taskId
     * @param variables
     * @param request
     * @return
     */
    @RequestMapping(value = "/claimAndComplete.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput claimAndComplete(@RequestParam String taskId, @RequestParam Map variables, HttpServletRequest request){
        String userId = "";
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            return BaseOutput.failure("用户未登录");
        }
        userId =  userTicket.getId().toString();
        // 当前用户申领任务
        taskService.claim(taskId, userId);
        taskService.setVariable(taskId, "taskId", taskId);
        // 完成任务
        taskService.complete(taskId, variables);
        return BaseOutput.success();
    }

    /**
     * 完成任务
     * @param taskId
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/complete.action", method = {RequestMethod.GET})
    public BaseOutput<String> complete(@RequestParam String taskId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        TaskDto task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        if(task == null){
//            System.out.println("任务不存在");
//            response.sendRedirect(request.getContextPath() + INDEX);
//            return;
//        }
//        if(StringUtils.isBlank(task.getAssignee())){
//            System.out.println("任务还未认领");
//            response.sendRedirect(request.getContextPath() + INDEX);
//            return;
//        }
        taskService.complete(taskId, variables);
        return BaseOutput.success();
    }

    /**
     * 完成表单任务
     * @param taskId
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/submitTaskFormData.action", method = {RequestMethod.GET})
    public void submitTaskFormData(@RequestParam String taskId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            System.out.println("任务不存在");
            response.sendRedirect(request.getContextPath() + INDEX);
            return;
        }
        if(StringUtils.isBlank(task.getAssignee())){
            System.out.println("任务还未认领");
            response.sendRedirect(request.getContextPath() + INDEX);
            return;
        }
//        taskService.complete(taskId, variables);
        formService.submitTaskFormData(taskId, variables);
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 转交任务
     * @param taskId    受理人
     * @param candidateId   转交人
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/setAssignee.action", method = {RequestMethod.GET})
    public void setAssignee(@RequestParam String taskId, @RequestParam String candidateId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        TaskDto task = taskService.createTaskQuery().taskAssignee(assigneeId).singleResult();
            taskService.setAssignee(taskId, candidateId);
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 委托任务
     * @param taskId        任务id
     * @param delegateId    被委托人
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/delegate.action", method = {RequestMethod.GET})
    public void delegate(@RequestParam String taskId, @RequestParam String delegateId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        taskService.delegateTask(taskId, delegateId);
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 解决委托任务
     * ps：1.有一点需要注意一下，Activiti是歪果仁写的，歪果仁与我们想法不太一样，
     * 他们认为委托任务必须有解决委托这一步骤，当解决委托后，流程并不是进行到下一个节点，
     * 而是需要被委托人有完成任务操作时方可进行到下一步，而中国式需求中大多都是解决委托就是完成任务，
     * 需要解决这个问题的话可以在调用解决委托后执行一个完成任务代码操作
     * @param taskId    任务id
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/resolve.action", method = {RequestMethod.GET})
    public void resolve(@RequestParam String taskId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //根据taskId提取任务
        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (StringUtils.isNotBlank(task.getOwner())) {
            DelegationState delegationState = task.getDelegationState();
            if (delegationState.toString().equals(DelegationState.RESOLVED.name())) {
                System.out.println("此委托任务已是完结状态");
            } else if (delegationState.toString().equals(DelegationState.PENDING.name())) {
                //如果是委托任务需要做处理
                taskService.resolveTask(taskId, variables);
            } else {
                System.out.println("此任务不是委托任务");
            }
        }
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 发JAVA接收任务信号
     * @param signalId
     * @param processInstanceId
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/signal.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> signal(@RequestParam String signalId, @RequestParam String processInstanceId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Execution> executions = runtimeService.createExecutionQuery()
          .processInstanceId(processInstanceId)
          .activityId(signalId)
          .list();
        if(executions == null || executions.isEmpty()){
            return BaseOutput.failure("信号["+signalId+"]发送失败");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("key1", "value1");
        param.put("key2", "value2");
        for(Execution execution : executions) {
            param.put(execution.getId(), execution.getActivityId());
            runtimeService.setVariablesLocal(execution.getId(), param);
            runtimeService.signal(execution.getId(), variables);
        }
        return BaseOutput.success("信号["+signalId+"]发送成功");
    }

    /**
     * 抛出信号事件
     * @param signalId
     * @param executionId
     * @param variables
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/signalEventReceived.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> signalEventReceived(@RequestParam String signalId, @RequestParam(required = false) String executionId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StringUtils.isBlank(executionId)){
            runtimeService.signalEventReceived(signalId, variables);
        }else {
            runtimeService.signalEventReceived(signalId, executionId, variables);
        }
        return BaseOutput.success("抛出信号["+signalId+"]发送成功");
    }

    /**
     * 抛出消息事件
     * @param messageName
     * @param processInstanceId
     * @param variables
     * @param request
     * @param response
     */
    @RequestMapping(value = "/messageEventReceived.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> messageEventReceived(@RequestParam String messageName, String processInstanceId, @RequestParam Map variables, HttpServletRequest request, HttpServletResponse response) {
        Execution execution = runtimeService.createExecutionQuery().messageEventSubscriptionName(messageName).processInstanceId(processInstanceId).singleResult();
        runtimeService.messageEventReceived(messageName, execution.getId(), variables);
        return BaseOutput.success("抛出消息["+messageName+"]发送成功");
    }

    /**
     * 获取任务表单信息
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/getTaskFormData.action", method = {RequestMethod.GET})
    @ResponseBody
    public BaseOutput<String> getTaskFormData(@RequestParam String taskId){
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        return BaseOutput.success(JSON.toJSONString(taskFormData.getFormProperties()));
    }

    /**
     * 获取任务变量
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/getVariables.action", method = {RequestMethod.GET})
    @ResponseBody
    public BaseOutput<Map<String, Object>> getVariables(@RequestParam String taskId){
        return BaseOutput.success().setData(taskService.getVariables(taskId));
    }

    /**
     * 提交任务表单信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/submitTaskFormData.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> submitTaskFormData(@RequestBody Map<String, String> params){
        String taskId = params.get("taskId");
        String variablesStr = params.get("variables");
        Map<String, String> variables = JSONObject.parseObject(variablesStr, new TypeReference<Map<String, String>>(){});
        try {
            formService.submitTaskFormData(taskId, variables);
            return BaseOutput.success("提交表单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.success("提交表单失败");
        }
    }
}
