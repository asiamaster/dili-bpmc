package com.dili.bpmc.sdk.rpc;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;

import java.util.Map;

/**
 * 运行时接口
 */
@Restful("${bpmc.server.address}")
public interface RuntimeRpc {

	/**
	 * 根据key和参数启动流程定义
	 * @param processDefinitionKey  流程定义key， 必填
	 * @param businessKey   业务key，选填
	 * @param variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GET("/api/runtime/startProcessInstanceByKey")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@ReqParam(value = "processDefinitionKey") String processDefinitionKey, @ReqParam(value = "businessKey", required = false) String businessKey, @ReqParam(value = "userId") String userId, @ReqParam(value = "variables") Map<String, Object> variables);

	/**
	 * 根据流程定义id和参数启动流程定义
	 * @param processDefinitionId 流程定义id
	 * @param businessKey   业务key，选填
	 * @param variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GET("/api/runtime/startProcessInstanceById")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceById(@ReqParam(value = "processDefinitionId") String processDefinitionId, @ReqParam(value = "businessKey", required = false) String businessKey, @ReqParam(value = "userId") String userId, @ReqParam(value = "variables") Map<String, Object> variables);

}
