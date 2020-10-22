package com.dili.bpmc.api;

import com.dili.bpmc.dao.EventSubscriptionMapper;
import com.dili.bpmc.sdk.domain.EventSubscriptionMapping;
import com.dili.ss.domain.BaseOutput;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 事件接口
 * 
 * @author asiamaster
 * @date 2020-10-20
 * @since 1.2
 */
@RestController
@RequestMapping("/api/event")
public class EventApi {
	private final Logger log = LoggerFactory.getLogger(EventApi.class);

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@SuppressWarnings("all")
	@Autowired
	private EventSubscriptionMapper eventSubscriptionMapper;

	/**
	 * 根据条件查询运行时边界事件
	 * @return
	 */
	@RequestMapping(value = "/listEventSubscription", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<EventSubscriptionMapping>> listEventSubscription(EventSubscriptionMapping eventSubscriptionMapping) {
		try {
			return BaseOutput.successData(eventSubscriptionMapper.select(eventSubscriptionMapping));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 根据流程实例id查询运行中的唯一任务的边界事件
	 *
	 * @param processInstanceId 必填
	 */
	@RequestMapping(value = "/listTaskEventsByProcessInstanceId", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<Event>> listTaskEventsByProcessInstanceId(@RequestParam String processInstanceId) {
		try {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
			List<Event> taskEvents = taskService.getTaskEvents(task.getId());
			return BaseOutput.successData(taskEvents);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 根据活动id和实例id，触发Java接收任务(Java Receive Task)
	 * 
	 * @param activityId        当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值，必填
	 * @param processInstanceId 必填
	 * @param variables
	 */
	@RequestMapping(value = "/signal", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> signal(@RequestParam String activityId, @RequestParam String processInstanceId, @RequestParam Map variables) {
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
	 * @param processInstanceId 选填
	 * @param variables
	 */
	@RequestMapping(value = "/signalEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> signalEventReceived(@RequestParam String signalName, @RequestParam(required = false) String executionId, @RequestParam(required = false) String processInstanceId, @RequestParam Map variables) {
		try {
			//首先根据executionId触发信号事件
			if (StringUtils.isNotBlank(executionId)) {
				runtimeService.signalEventReceived(signalName, executionId, variables);
				//没有executionId，则根据processInstanceId和signalName获取executionId
			} else if (StringUtils.isNotBlank(processInstanceId)) {
				Execution execution = runtimeService.createExecutionQuery().signalEventSubscriptionName(signalName).processInstanceId(processInstanceId).singleResult();
				if (execution == null) {
					return BaseOutput.failure("不存在执行的流程");
				}
				runtimeService.signalEventReceived(signalName, execution.getId(), variables);
			}else{
				//全局广播
				runtimeService.signalEventReceived(signalName, variables);
			}
			return BaseOutput.success("抛出信号[" + signalName + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 抛出(边界)消息事件
	 * 
	 * @param messageName       必填
	 * @param processInstanceId 必填
	 * @param variables
	 */
	@RequestMapping(value = "/messageEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> messageEventReceived(@RequestParam String messageName,@RequestParam String processInstanceId, @RequestParam Map<String, Object> variables) {
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

}
