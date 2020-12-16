package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.EventSubscriptionMapping;
import com.dili.bpmc.sdk.dto.EventReceivedDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.VOBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 事件接口
 */
@FeignClient(name = "dili-bpmc", contextId = "event-service")
public interface EventFeignRpc {

	/**
	 * 根据条件查询边界事件
	 *
	 * @param eventSubscriptionMapping
	 */
	@PostMapping("/api/event/listEventSubscription")
	BaseOutput<List<EventSubscriptionMapping>> listEventSubscription(@RequestBody EventSubscriptionMapping eventSubscriptionMapping);

	/**
	 * 根据流程实例id查询边界事件
	 *
	 * @param processInstanceId 必填
	 */
	@GetMapping("/api/event/listEventSubscription")
	BaseOutput<List<EventSubscriptionMapping>> listEventSubscription(@RequestParam("processInstanceId") String processInstanceId);

	/**
	 * 根据流程实例id查询边界事件名称列表
	 *
	 * @param processInstanceId 必填
	 */
	@GetMapping("/api/event/listEventName")
	BaseOutput<List<String>> listEventName(@RequestParam("processInstanceId") String processInstanceId);

	/**
	 * 抛出消息事件
	 * 
	 * @param eventReceivedDto messageName       必填
	 * @param eventReceivedDto processInstanceId或executionId 必填一项
	 */
	@PostMapping("/api/event/messageEventReceived")
	BaseOutput<String> messageEventReceived(@RequestBody EventReceivedDto eventReceivedDto);

	/**
	 * 根据signalName和executionId触发信号事件 executionId为空，则在全局范围广播，为所有已订阅处理器抛出信号（广播）
	 * executionId不为空， 只为指定的执行传递信号
	 *
	 * @param eventReceivedDto eventName 必填
	 * @param eventReceivedDto processInstanceId 选填
	 */
	@PostMapping("/api/event/signalEventReceived")
	BaseOutput<String> signalEventReceived(@RequestBody EventReceivedDto eventReceivedDto);

	/**
	 * 根据活动id和实例id，触发Java接收任务(Java Receive Task)
	 *
	 * @param eventReceivedDto eventName        当前活动的id，对应receiveTask.bpmn文件中的活动节点的id的属性值，必填
	 * @param eventReceivedDto processInstanceId或executionId 必填一项
	 * @return
	 */
	@PostMapping("/api/event/signal")
	BaseOutput<String> signal(@RequestBody EventReceivedDto eventReceivedDto);


}
