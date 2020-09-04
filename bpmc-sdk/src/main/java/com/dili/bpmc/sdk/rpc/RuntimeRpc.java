package com.dili.bpmc.sdk.rpc;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 运行时接口
 */
@Restful("${bpmc.server.address}")
public interface RuntimeRpc {

	/**
	 * 根据流程实例id更新businessKey
	 * @param processInstanceId
	 * @param businessKey
	 * @return
	 */
	@GET("/api/runtime/updateBusinessKey")
	BaseOutput updateBusinessKey(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "businessKey") String businessKey);

	/**
	 * 根据key和参数启动流程定义
	 * @param processDefinitionKey  流程定义key， 必填
	 * @param businessKey   业务key，选填
	 * @param variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GET("/api/runtime/startProcessInstanceByKey")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@ReqParam(value = "processDefinitionKey") String processDefinitionKey, @ReqParam(value = "businessKey", required = false) String businessKey, @ReqParam(value = "userId") String userId, @ReqParam(value = "variables") Map<String, String> variables);

	/**
	 * 根据流程定义id和参数启动流程定义
	 * @param processDefinitionId 流程定义id
	 * @param businessKey   业务key，选填
	 * @param variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GET("/api/runtime/startProcessInstanceById")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceById(@ReqParam(value = "processDefinitionId") String processDefinitionId, @ReqParam(value = "businessKey", required = false) String businessKey, @ReqParam(value = "userId") String userId, @ReqParam(value = "variables") Map<String, String> variables);

	/**
	 * 结束流程实例
	 * @param processInstanceId	流程实例id
	 * @param deleteReason 结束原因
	 * @return
	 */
	@GET("/api/runtime/stopProcessInstanceById")
	BaseOutput<ProcessInstanceMapping> stopProcessInstanceById(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "deleteReason", required = false) String deleteReason);

}
