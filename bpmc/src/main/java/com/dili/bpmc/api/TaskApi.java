package com.dili.bpmc.api;

import com.alibaba.fastjson.JSON;
import com.dili.bpmc.dao.ActRuTaskMapper;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.google.common.collect.Lists;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 任务接口
 * 
 * @author asiamaster
 * @date 2019-11-29
 * @since 1.0
 */
@RestController
@RequestMapping("/api/task")
public class TaskApi {
	private final Logger log = LoggerFactory.getLogger(TaskApi.class);
//    @Autowired
//    private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
//    @Autowired
//    private HistoryService historyService;
//    @Autowired
//    private IdentityService identityService;
//    @Autowired
//    private ActivitiService activitiService;
	@Autowired
	private ActRuTaskMapper actRuTaskMapper;

	/**
	 * 申领(签收)任务
	 * 
	 * @param userId 申领人，为空则默认为当前登录用户,不填为当前UAP用户
	 * @param taskId 任务id 必填
	 */
	@RequestMapping(value = "/claim", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> claim(@RequestParam String taskId, @RequestParam(required = false) String userId) {
		try {
			taskService.claim(taskId, userId);
			return BaseOutput.success();
		} catch (Exception e) {
			// 可能任务已被其他人签收
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId    必填
	 * @param assignee  强制插手认领人
	 * @param variables
	 * @throws IOException
	 * @return taskId
	 */
	@RequestMapping(value = "/complete", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> complete(@RequestParam String taskId, @RequestParam(required = false) String assignee, @RequestParam Map<String, String> variables) {
		// 强制插手人签收任务
		if (StringUtils.isNotBlank(assignee)) {
			taskService.claim(taskId, assignee);
		}
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return BaseOutput.failure("任务不存在");
		}
		if (StringUtils.isBlank(task.getAssignee())) {
			return BaseOutput.failure("任务还未认领");
		}
		taskService.complete(taskId, (Map)variables);
		return BaseOutput.successData(taskId);
	}

	/**
	 * 强制提交任务，使用于无办理人的场景
	 * 
	 * @param taskId    必填
	 * @param variables
	 */
	@RequestMapping(value = "/completeByForce", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> completeByForce(@RequestParam String taskId, @RequestParam Map<String, Object> variables) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return BaseOutput.failure("任务不存在");
		}
		taskService.complete(taskId, variables);
		return BaseOutput.success();
	}

	/**
	 * 提交任务表单
	 * 
	 * @param taskId    必填
	 * @param assignee  强制插手认领人
	 * @param variables
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping(value = "/submitTaskForm", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> submitTaskForm(@RequestParam String taskId, @RequestParam(required = false) String assignee, @RequestParam Map<String, String> variables, HttpServletRequest request) {
		// 强制插手人签收任务
		if (StringUtils.isNotBlank(assignee)) {
			taskService.claim(taskId, assignee);
		}
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return BaseOutput.failure("任务不存在");
		}
		if (StringUtils.isBlank(task.getAssignee())) {
			return BaseOutput.failure("任务还未认领");
		}
		formService.submitTaskFormData(taskId, variables);
		return BaseOutput.success();
	}

	/**
	 * 转交任务
	 * 
	 * @param taskId      任务id，必填
	 * @param candidateId 转交人,必填
	 */
	@RequestMapping(value = "/setAssignee", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> setAssignee(@RequestParam String taskId, @RequestParam String candidateId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return BaseOutput.failure("任务不存在");
		}
		taskService.setAssignee(taskId, candidateId);
		return BaseOutput.success();
	}

	/**
	 * 委托任务 Delegates the task to another user. This means that the assignee is set
	 * and the delegation state is set to {@link DelegationState#PENDING}. If no
	 * owner is set on the task, the owner is set to the current assignee of the
	 * task.
	 * 
	 * @param taskId     任务id，必填
	 * @param delegateId 被委托人， 必填
	 */
	@RequestMapping(value = "/delegate", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> delegate(@RequestParam String taskId, @RequestParam String delegateId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return BaseOutput.failure("任务不存在");
		}
		taskService.delegateTask(taskId, delegateId);
		return BaseOutput.success();
	}

	/**
	 * 解决委托任务 ps：1.有一点需要注意一下，Activiti是歪果仁写的，歪果仁与我们想法不太一样，
	 * 他们认为委托任务必须有解决委托这一步骤，当解决委托后，流程并不是进行到下一个节点，
	 * 而是需要被委托人有完成任务操作时方可进行到下一步，而中国式需求中大多都是解决委托就是完成任务，
	 * 需要解决这个问题的话可以在调用解决委托后执行一个完成任务代码操作
	 * 
	 * @param taskId    任务id，必填
	 * @param variables
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping(value = "/resolve", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> resolve(@RequestParam String taskId, @RequestParam Map variables, HttpServletRequest request) {
		// 根据taskId提取任务
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task == null){
			return BaseOutput.failure("任务不存在");
		}
		if (StringUtils.isNotBlank(task.getOwner())) {
			DelegationState delegationState = task.getDelegationState();
			if (delegationState.toString().equals(DelegationState.RESOLVED.name())) {
				return BaseOutput.failure("此委托任务已是完结状态");
			} else if (delegationState.toString().equals(DelegationState.PENDING.name())) {
				// 如果是委托任务需要做处理
				taskService.resolveTask(taskId, variables);
			} else {
				return BaseOutput.failure("此任务不是委托任务");
			}
		}
		return BaseOutput.success();
	}

	/**
	 * 根据活动id和实例id，触发信号事件
	 * 
	 * @param activityId        当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值，必填
	 * @param processInstanceId 必填
	 * @param variables
	 * @param request
	 */
	@RequestMapping(value = "/signal", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> signal(@RequestParam String activityId, @RequestParam String processInstanceId, @RequestParam Map variables, HttpServletRequest request) {
		try {
			Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).activityId(activityId)// 当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值
					.singleResult();
			if (execution == null) {
				return BaseOutput.failure("信号[" + activityId + "]发送失败");
			}
			runtimeService.signal(execution.getId(), variables);
			return BaseOutput.success("信号[" + activityId + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 根据signalName和executionId触发信号事件 executionId为空，则在全局范围广播，为所有已订阅处理器抛出信号（广播）
	 * executionId不为空， 只为指定的执行传递信号
	 * 
	 * @param signalName  必填
	 * @param executionId 选填
	 * @param variables
	 * @param request
	 */
	@RequestMapping(value = "/signalEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> signalEventReceived(@RequestParam String signalName, @RequestParam(required = false) String executionId, @RequestParam Map variables, HttpServletRequest request) {
		try {
			if (StringUtils.isBlank(executionId)) {
				runtimeService.signalEventReceived(signalName, variables);
			} else {
				runtimeService.signalEventReceived(signalName, executionId, variables);
			}
			return BaseOutput.success("抛出信号[" + signalName + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 抛出消息事件
	 * 
	 * @param messageName       必填
	 * @param processInstanceId 必填
	 * @param variables
	 * @param request
	 */
	@RequestMapping(value = "/messageEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> messageEventReceived(@RequestParam String messageName, String processInstanceId, @RequestParam Map<String, Object> variables, HttpServletRequest request) {
		try {
			Execution execution = runtimeService.createExecutionQuery().messageEventSubscriptionName(messageName).processInstanceId(processInstanceId).singleResult();
			if (execution == null) {
				return BaseOutput.failure("不存在执行的流程");
			}
			runtimeService.messageEventReceived(messageName, execution.getId(), variables);
			return BaseOutput.success("抛出消息[" + messageName + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 获取activiti原生任务表单信息
	 * 
	 * @param taskId，必填
	 * @return List<org.activiti.engine.form.FormProperty>
	 */
	@RequestMapping(value = "/getTaskFormData", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<String> getTaskFormData(@RequestParam String taskId) {
		TaskFormData taskFormData = formService.getTaskFormData(taskId);
		if (taskFormData == null) {
			return BaseOutput.failure("表单信息不存在");
		}
		return BaseOutput.success(JSON.toJSONString(taskFormData.getFormProperties()));
	}

	/**
	 * 获取任务变量
	 * 
	 * @param taskId，必填
	 * @return
	 */
	@RequestMapping(value = "/getVariables", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Map<String, Object>> getVariables(@RequestParam String taskId) {
		return BaseOutput.success().setData(taskService.getVariables(taskId));
	}

	/**
	 * 设置本地任务变量
	 *
	 * @param taskId，必填
	 * @return
	 */
	@RequestMapping(value = "/setVariablesLocal", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput setVariablesLocal(@RequestParam String taskId, @RequestParam Map<String, String> variables) {
		variables.remove("taskId");
		taskService.setVariablesLocal(taskId, variables);
		return BaseOutput.success();
	}


	/**
	 * 获取任务变量
	 * 
	 * @param taskId       任务id，必填
	 * @param variableName 变量名，必填
	 * @return
	 */
	@RequestMapping(value = "/getVariable", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> getVariable(@RequestParam String taskId, @RequestParam String variableName) {
		return BaseOutput.success().setData(taskService.getVariable(taskId, variableName));
	}

	/**
	 * 根据任务id查询任务
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/getById", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<TaskMapping> getById(@RequestParam String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task == null){
			return BaseOutput.failure("任务不存在");
		}
		return BaseOutput.success().setData(DTOUtils.as(task, TaskMapping.class));
	}

	/**
	 * 查询任务列表
	 * 
	 * @param taskDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<TaskMapping>> list(TaskDto taskDto) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (StringUtils.isNotBlank(taskDto.getAssignee())) {
			taskQuery.taskAssignee(taskDto.getAssignee());
		}
		if (StringUtils.isNotBlank(taskDto.getProcessDefinitionId())) {
			taskQuery.processDefinitionId(taskDto.getProcessDefinitionId());
		}
		if (StringUtils.isNotBlank(taskDto.getProcessInstanceId())) {
			taskQuery.processInstanceId(taskDto.getProcessInstanceId());
		}
		if (StringUtils.isNotBlank(taskDto.getTaskId())) {
			taskQuery.taskId(taskDto.getTaskId());
		}
		if (StringUtils.isNotBlank(taskDto.getTaskDefinitionKey())) {
			taskQuery.taskDefinitionKey(taskDto.getTaskDefinitionKey());
		}
		if (StringUtils.isNotBlank(taskDto.getCandidateUser())) {
			taskQuery.taskCandidateUser(taskDto.getCandidateUser());
		}
		if (taskDto.getProcessVariables() != null) {
			for (Map.Entry<String, Object> entry : taskDto.getProcessVariables().entrySet()) {
				taskQuery.processVariableValueEquals(entry.getKey(), entry.getValue());
			}
		}
		if (taskDto.getTaskVariables() != null) {
			for (Map.Entry<String, Object> entry : taskDto.getTaskVariables().entrySet()) {
				taskQuery.taskVariableValueEquals(entry.getKey(), entry.getValue());
			}
		}
		if (StringUtils.isNotBlank(taskDto.getProcessInstanceBusinessKey())) {
			taskQuery.processInstanceBusinessKey(taskDto.getProcessInstanceBusinessKey());
		}
		List<Task> taskList = taskQuery.list();
//        List<TaskMapping> taskMappingList = new ArrayList<>(taskList.size());
//        for(Task task : taskList){
//            TaskMapping taskMapping = DTOUtils.asInstance(task, TaskMapping.class);
//            taskMapping.setId(task.getId());
//            taskMappingList.add(taskMapping);
//        }
		return BaseOutput.success().setData(DTOUtils.asInstance(taskList, TaskMapping.class));
	}

	/**
	 * 使用mybatis自定义任务查询
	 * 
	 * @param taskDto 任务查询对象
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listTaskMapping", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<TaskMapping>> listTaskMapping(TaskDto taskDto) {
		List<TaskMapping> list = actRuTaskMapper.list(taskDto);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 根据流程实例id批量查询任务候选人、候选组、办理人
	 * 
	 * @param processIntanceIds
	 * @return
	 */
	@PostMapping("/listTaskIdentityByProcessInstanceIds")
	public BaseOutput<List<TaskIdentityDto>> listTaskIdentityByProcessInstanceIds(@RequestBody List<String> processIntanceIds) {
		return BaseOutput.successData(this.actRuTaskMapper.listTaskIdentityByProcessInstanceIds(processIntanceIds));
	}

	/**
	 * 根据流程实例id批量查询任务候选人、候选组、办理人
	 *
	 * @param processIntanceIds
	 * @return
	 */
	@PostMapping("/listTaskIdentityByProcessInstanceId")
	public BaseOutput<List<TaskIdentityDto>> listTaskIdentityByProcessInstanceId(@RequestBody String processIntanceIds) {
		return BaseOutput.successData(this.actRuTaskMapper.listTaskIdentityByProcessInstanceIds(Lists.newArrayList(processIntanceIds)));
	}

}
