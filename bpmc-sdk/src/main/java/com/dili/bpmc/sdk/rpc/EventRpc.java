package com.dili.bpmc.sdk.rpc;

import com.dili.bpmc.sdk.domain.EventSubscriptionMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 事件接口
 */
@Restful("${bpmc.server.address}")
public interface EventRpc {

	/**
	 * 根据条件查询边界事件
	 *
	 * @param eventSubscriptionMapping
	 */
	@POST("/api/event/listEventSubscription")
	BaseOutput<List<EventSubscriptionMapping>> listEventSubscription(@VOBody EventSubscriptionMapping eventSubscriptionMapping);

	/**
	 * 根据流程实例id查询边界事件
	 *
	 * @param processInstanceId 必填
	 */
	@GET("/api/event/listEventSubscription")
	BaseOutput<List<EventSubscriptionMapping>> listEventSubscription(@ReqParam("processInstanceId") String processInstanceId);

	/**
	 * 根据流程实例id查询边界事件名称列表
	 *
	 * @param processInstanceId 必填
	 */
	@GET("/api/event/listEventName")
	BaseOutput<List<String>> listEventName(@ReqParam("processInstanceId") String processInstanceId);

	/**
	 * 抛出消息事件
	 * 
	 * @param messageName       必填
	 * @param processInstanceId 必填
	 * @param variables
	 */
	@POST("/api/event/messageEventReceived")
	BaseOutput<String> messageEventReceived(@ReqParam(value = "messageName") String messageName, @ReqParam(value = "processInstanceId") String processInstanceId,
			@ReqParam(value = "variables", required = false) Map<String, Object> variables);

	/**
	 * 触发Java接收任务
	 * @param processInstanceId
	 * @param activityId
	 * @param variables
	 * @return
	 */
	@POST("/api/event/signal")
	BaseOutput<String> signal(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId") String activityId, @ReqParam(value = "variables", required = false) Map<String, String> variables);


}
