package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.bpmc.consts.TaskCategory;
import com.dili.bpmc.dao.ActRuTaskMapper;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.service.ActFormService;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.rpc.RoleRpc;
import com.dili.uap.sdk.session.SessionContext;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
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
    private ActFormService actFormService;
    @Autowired
    @SuppressWarnings("all")
    private RoleRpc roleRpc;
    @Autowired
    @SuppressWarnings("all")
    private ActRuTaskMapper actRuTaskMapper;

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
     * @param category 页面类型(inbox(默认), tasks, queued, involved和archived)
     * @param groupId 当category=queued时，group为用户组id
     * @param taskId 任务id， 选填，用于显示指定任务详情
     * @return
     */
    @RequestMapping(value = "/taskCenter.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String taskCenter(@RequestParam(required = false) String taskId,
                        @RequestParam(defaultValue = "inbox") String category,
                        @RequestParam(required = false) String groupId,
                        HttpServletRequest request) {
        handleTaskCategory(category, taskId, groupId, request);
        return "process/taskCenter";
    }

//    /**
//     * 查询任务列表
//     * @param userId 用户ID
//     * @param category 页面类型(inbox(默认), tasks, queued, involved和archived)
//     * @param groupId 用户组ID
//     * @param taskId 用于右侧任务详情面板显示指定的任务，选填，默认为第一个任务
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value="/taskList.action", method = {RequestMethod.GET, RequestMethod.POST})
//    public @ResponseBody String taskList(@RequestParam String category, @RequestParam(required = false) String userId, @RequestParam(required = false) String groupId, @RequestParam(required = false) String taskId) throws Exception {
//        //如果没有userId参数，则使用当前登录的用户
//        if(StringUtils.isBlank(userId)){
//            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
//            if(userTicket == null){
//                throw new NotLoginException();
//            }
//            userId = userTicket.getId().toString();
//        }
//        //查询任务列表，用于左侧任务列表显示
//        List<Task> tasks = listTaskByCategory(category, userId, groupId);
//        PropertyFilter proFileter = new PropertyFilter() {
//            @Override
//            public boolean apply(Object object, String name, Object value) {
//                if("id".equalsIgnoreCase(name) || "name".equalsIgnoreCase(name)){
//                    return true;
//                }
//                return false;
//            }
//        };
//        return JSON.toJSONString(tasks, proFileter, SerializerFeature.IgnoreErrorGetter);
//    }

    /**
     * 查询任务列表
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
        List list = taskQuery.orderByTaskCreateTime().desc().listPage(firstResult, param.getRows());
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
        return "redirect:"+request.getContextPath()+"/actForm/dynamicForm.html";
    }

    /**
     * 申领(签收)任务
     * @param userId    申领人，为空则默认为当前登录用户
     * @param taskId    任务id
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/claim.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput claim(@RequestParam String taskId, @RequestParam(required = false) String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        TaskDto task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();
        if(StringUtils.isBlank(userId)) {
            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
            if (userTicket == null) {
                throw new NotLoginException();
            }
            userId = userTicket.getId().toString();
        }
        taskService.claim(taskId, userId);
        return BaseOutput.success();
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
        for(Execution execution : executions) {
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

    /**
     * 统一处理不同类别的任务
     * @param category  任务类别
     * @param taskId 任务id
     * @param groupId 受邀用户组
     */
    private void handleTaskCategory(String category, String taskId, String groupId, HttpServletRequest request){
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            throw new NotLoginException();
        }
        //当前用户为办理人
        String userId = userTicket.getId().toString();
        //待办任务
//        指派给当前登录用户的任务
        long inboxCount = taskService.createTaskQuery().taskAssignee(userId).count();
        //我的任务
//        属主的任务
//        （委托人）：受理人委托其他人操作该TASK的时候，受理人就成了委托人OWNER_，其他人就成了受理人ASSIGNEE_
        long tasksCount = taskService.createTaskQuery().taskOwner(userId).count();
        BaseOutput<List<Role>> rolesOutput = roleRpc.listByUser(Long.valueOf(userId), null);
        if(!rolesOutput.isSuccess()){
            throw new AppException("远程查询角色失败");
        }
        //设置选择类别，用于高亮显示被选中的类别
        request.setAttribute("category", category);
        //队列任务
//        候选用户组下的任务
        long queuedCount = 0;
        List<Role> roles = rolesOutput.getData();

        //记录当前用户的所有用户组，用于查询左侧任务列表显示
        String[] groupIds = new String[roles.size()];
        for(int i=0; i<roles.size(); i++) {
            Role role = roles.get(i);
            groupIds[i] = role.getId().toString();
        }
        //用户组信息，用于队列右键菜单显示(注意，在用户有多个角色，并且多个角色都有相同的任务时会重复)
        Map<String, String> groupMap = new HashMap<>(roles.size());
        //统计每个用户组的任务数
        for(Role role : roles) {
            long count = taskService.createTaskQuery().taskCandidateGroup(role.getId().toString()).count();
            queuedCount += count;
            groupMap.put(role.getId().toString(), new StringBuilder().append(role.getRoleName()).append("[").append(count).append("]").toString());
        }
        //队列右键菜单
        request.setAttribute("groupMap", groupMap);

        //受邀任务，这里包括候选用户任务和候选组任务
//        long involvedCount = taskService.createTaskQuery().taskCandidateUser(userId).count();
        //受邀任务，这里只包括候选用户任务
        long involvedCount = actRuTaskMapper.taskCandidateUserCount(userId);
        //查询已归档任务数
        long archivedCount = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).count();

        //设置标题部分显示的任务数
        request.setAttribute("inboxCount", inboxCount);
        request.setAttribute("tasksCount", tasksCount);
        request.setAttribute("queuedCount", queuedCount);
        request.setAttribute("involvedCount", involvedCount);
        request.setAttribute("archivedCount", archivedCount);

        //查询任务列表，用于左侧任务列表显示
        //设置当前显示的任务，用于在点击时直接跳转到该任务
        TaskInfo task = null;
        //组装左侧任务列表
        JSONArray ja;
        //先判断是否已归档类型，单独处理
        if(TaskCategory.ARCHIVED.getCode().equals(category)){
            //只查50条已归档数据;
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).listPage(0, 50);
            task = findTaskById(historicTaskInstances, taskId);
            ja = buildTaskList(historicTaskInstances);
        }else {
            //判断有groupId，并且当前用户也有该组权限, 没有groupId则列出当前用户组下所有任务
            List<Task> tasks = containsGroupId(groupIds, groupId) ? listTaskByCategory(category, userId, groupId) : listTaskByCategory(category, userId, groupIds);
            task = findTaskById(tasks, taskId);
            ja = buildTaskList(tasks);
        }
        if (task == null) {
            return;
        }
        //设置左侧任务列表
        request.setAttribute("tasks", ja.toJSONString());
        //设置流程发起人(这个会稍微影响性能，暂时不开放)
//        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//        request.setAttribute("startUserId", historicProcessInstance.getStartUserId());
        //判断当前用户是否是办理人，如果不是则需要屏蔽任务表单
        if(userId.equals(task.getAssignee())){
            request.setAttribute("isAssignee", true);
        }
        //设置中间部分的任务详情
        request.setAttribute("task", task);

        //查询并设置任务所属流程名称
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        request.setAttribute("processDefinitionName", processDefinition == null || processDefinition.getName() == null ? "无所属流程定义" :processDefinition.getName());
        //非归档任务才显示表单信息
        if(!TaskCategory.ARCHIVED.getCode().equals(category)) {
            //表单key，用于显示任务内容
            String formKey = task.getFormKey();
            if (formKey == null) {
//            throw new ParamErrorException("任务["+task.getId()+"]的formKey不存在");
                return;
            }
            ActForm actForm = actFormService.getByKey(formKey);
            if (actForm == null) {
//            throw new ParamErrorException("任务表单["+formKey+"]不存在");
                return;
            }
            request.setAttribute("actForm", actForm);
        }
    }

    /**
     * 构建任务列表数据
     * @param tasks
     * @param <T>
     * @return
     */
    private <T extends TaskInfo> JSONArray buildTaskList(List<T> tasks){
        //组装左侧任务列表
        JSONArray ja = new JSONArray();
        Map<String, String> processDefinitionMap = new HashMap<>();
        for(TaskInfo t : tasks){
            JSONObject jo = new JSONObject();
            jo.put("id", t.getId());
            jo.put("name", t.getName());
            //转换流程定义id为名称
            if(!processDefinitionMap.containsKey(t.getProcessDefinitionId())){
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(t.getProcessDefinitionId()).singleResult();
                //流程定义可能被删除了，所以要判空
                if(processDefinition != null){
                    processDefinitionMap.put(processDefinition.getId(), processDefinition.getName());
                }
            }
            jo.put("processDefinitionName", processDefinitionMap.get(t.getProcessDefinitionId()));
            ja.add(jo);
        }
        return ja;
    }
    /**
     * 根据任务id从任务列表获取任务，如果任务id为空，则取第一个任务
     * @param tasks
     * @param taskId
     * @param <T>
     * @return
     */
    private <T extends TaskInfo> T findTaskById(List<T> tasks, String taskId){
        T task = null;
        if (!CollectionUtils.isEmpty(tasks)) {
            if (StringUtils.isBlank(taskId)) {
                task = tasks.get(0);
            } else {
                for (T t : tasks) {
                    if (taskId.equals(t.getId())) {
                        task = t;
                        break;
                    }
                }
            }
        }
        return task;
    }

    /**
     * 判断是否包含组id
     * @param groupIds
     * @param groupId
     * @return
     */
    private boolean containsGroupId(String[] groupIds, String groupId){
        if(StringUtils.isBlank(groupId)){
            return false;
        }
        for(String s : groupIds){
            if(s.equals(groupId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据类别和用户查询任务列表
     * @param category
     * @param userId
     * @param groupIds 用户组id列表，用于类别是任务队列时。
     * @return
     */
    private List<Task> listTaskByCategory(String category, String userId, String... groupIds){
        TaskQuery taskQuery = taskService.createTaskQuery().orderByTaskCreateTime().desc();
        List<Task> tasks = new ArrayList<>();
        if(TaskCategory.INBOX.getCode().equals(category)){
            tasks = taskQuery.taskAssignee(userId).list();
        }else if(TaskCategory.TASKS.getCode().equals(category)){
            tasks = taskQuery.taskOwner(userId).list();
        }else if(TaskCategory.QUEUED.getCode().equals(category)){
            if(groupIds != null) {
                for(String groupId : groupIds) {
                    tasks.addAll(taskQuery.taskCandidateGroup(groupId).list());
                }
            }
        }else if(TaskCategory.INVOLVED.getCode().equals(category)){
            tasks = taskQuery.taskCandidateUser(userId).list();
        }
        return tasks;
    }

}
