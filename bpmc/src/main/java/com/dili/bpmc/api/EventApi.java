package com.dili.bpmc.api;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.dili.bpmc.consts.EventType;
import com.dili.bpmc.dao.EventSubscriptionMapper;
import com.dili.bpmc.sdk.domain.EventSubscriptionMapping;
import com.dili.bpmc.sdk.dto.EventReceivedDto;
import com.dili.ss.domain.BaseOutput;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
	@Autowired
	private IdentityService identityService;
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
	 * 根据条件查询运行时边界事件名称列表
	 * @return
	 */
	@SentinelResource("listEventName")
	@RequestMapping(value = "/listEventName", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<String>> listEventName(EventSubscriptionMapping eventSubscriptionMapping) {
		try {
			List<String> list = eventSubscriptionMapper.select(eventSubscriptionMapping).stream().map(t -> t.getEventName()).collect(Collectors.toList());
			return BaseOutput.successData(list);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 根据活动id和实例id，触发Java接收任务(Java Receive Task)
	 * 
	 * @param eventReceivedDto eventName        当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值，必填
	 * @param eventReceivedDto processInstanceId或executionId 必填一项
	 */
	@RequestMapping(value = "/signal", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> signal(EventReceivedDto eventReceivedDto) {
		try {
			eventReceivedDto.setEventType(EventType.JAVA_RECEIVE_TASK);
			String executionId = getExecutionId(eventReceivedDto);
			if (executionId == null) {
				return BaseOutput.failure("信号[" + eventReceivedDto.getEventName() + "]发送失败，不存在执行的流程");
			}
			if(eventReceivedDto.getUserId() != null) {
				identityService.setAuthenticatedUserId(eventReceivedDto.getUserId());
			}
			runtimeService.signal(executionId, eventReceivedDto.getVariables());
			return BaseOutput.success("信号[" + eventReceivedDto.getEventName() + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}


	/**
	 * 根据signalName和executionId触发信号事件 executionId为空，则在全局范围广播，为所有已订阅处理器抛出信号（广播）
	 * executionId不为空， 只为指定的执行传递信号
	 *
	 * @param eventReceivedDto eventName 必填
	 * @param eventReceivedDto processInstanceId 选填
	 */
	@RequestMapping(value = "/signalEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> signalEventReceived(EventReceivedDto eventReceivedDto) {
		try {
			eventReceivedDto.setEventType(EventType.SIGNAL);
			String executionId = getExecutionId(eventReceivedDto);
			if(eventReceivedDto.getUserId() != null) {
				identityService.setAuthenticatedUserId(eventReceivedDto.getUserId());
			}
			//根据executionId触发信号事件
			if (StringUtils.isNotBlank(executionId)) {
				runtimeService.signalEventReceived(eventReceivedDto.getEventName(), executionId, eventReceivedDto.getVariables());
			}
			//全局广播
			else{
				runtimeService.signalEventReceived(eventReceivedDto.getEventName(), eventReceivedDto.getVariables());
			}
			return BaseOutput.success("抛出信号[" + eventReceivedDto.getEventName() + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 抛出(边界)消息事件
	 *
	 * @param eventReceivedDto eventName       必填
	 * @param eventReceivedDto processInstanceId或executionId 必填一项
	 */
	@RequestMapping(value = "/messageEventReceived", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public BaseOutput<String> messageEventReceived(EventReceivedDto eventReceivedDto) {
		try {
			eventReceivedDto.setEventType(EventType.MESSAGE);
			String executionId = getExecutionId(eventReceivedDto);
			if (executionId == null) {
				return BaseOutput.failure("消息[" + eventReceivedDto.getEventName() + "]发送失败，不存在执行的流程");
			}
			if(eventReceivedDto.getUserId() != null) {
				identityService.setAuthenticatedUserId(eventReceivedDto.getUserId());
			}
			runtimeService.messageEventReceived(eventReceivedDto.getEventName(), executionId, eventReceivedDto.getVariables());
			return BaseOutput.success("抛出消息[" + eventReceivedDto.getEventName() + "]发送成功");
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 获取执行id
	 * @param eventReceivedDto
	 * @return
	 */
	private String getExecutionId(EventReceivedDto eventReceivedDto){
		if (StringUtils.isNotBlank(eventReceivedDto.getExecutionId())) {
			return eventReceivedDto.getExecutionId();
		}
		if(StringUtils.isBlank(eventReceivedDto.getProcessInstanceId()) || StringUtils.isBlank(eventReceivedDto.getEventName())){
			return null;
		}
		ExecutionQuery executionQuery = runtimeService.createExecutionQuery().processInstanceId(eventReceivedDto.getProcessInstanceId());
		switch (eventReceivedDto.getEventType()){
			case EventType.MESSAGE:
				executionQuery.messageEventSubscriptionName(eventReceivedDto.getEventName());
				break;
			case EventType.SIGNAL:
				executionQuery.signalEventSubscriptionName(eventReceivedDto.getEventName());
				break;
			case EventType.JAVA_RECEIVE_TASK:
				executionQuery.activityId(eventReceivedDto.getEventName());
				break;
		}
		Execution execution = executionQuery.singleResult();
		if (execution == null) {
			return null;
		}
		return execution.getId();
	}
}
