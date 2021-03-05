package com.dili.bpmc.sdk.rpc.restful;

import com.dili.bpmc.sdk.domain.ExecutionMapping;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.HistoricProcessInstanceQueryDto;
import com.dili.bpmc.sdk.dto.ProcessInstanceVariablesDto;
import com.dili.bpmc.sdk.dto.StartProcessInstanceDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.*;
import com.dili.ss.seata.annotation.GlobalTx;

import java.util.List;
import java.util.Map;

/**
 * 运行时接口
 */
@Restful("${bpmc.server.address}")
public interface RuntimeRpc {

	/**
	 * 根据实例id查询进行中的执行
	 * @param processInstanceId
	 * @return
	 */
	@GET("/api/runtime/listExecution")
	BaseOutput<List<ExecutionMapping>> listExecution(@ReqParam(value = "processInstanceId") String processInstanceId);

	/**
	 * 获取流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @return
	 */
	@GET("/api/runtime/getVariables")
	BaseOutput<Map<String, Object>> getVariables(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId", required = false) String activityId);

	/**
	 * 设置流程变量
	 * @param setProcessInstanceVariablesDto processInstanceId
	 * @param setProcessInstanceVariablesDto activityId
	 * @param setProcessInstanceVariablesDto variables
	 * @return
	 */
	@POST("/api/runtime/setVariables")
	@GlobalTx
	BaseOutput setVariables(@VOBody ProcessInstanceVariablesDto setProcessInstanceVariablesDto);

	/**
	 * 设置流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @param key
	 * @param value
	 * @return
	 */
	@GET("/api/runtime/setVariable")
	@GlobalTx
	BaseOutput setVariable(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId") String activityId, @ReqParam(value = "key") String key, @ReqParam(value = "value") String value);

	/**
	 * 删除流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @param key
	 * @return
	 */
	@GET("/api/runtime/removeVariable")
	@GlobalTx
	BaseOutput removeVariable(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId") String activityId, @ReqParam(value = "key") String key);

	/**
	 * 根据流程实例id或businessKey查询进行中的流程实例
	 * 两个参数至少传一个
	 * @param processInstanceId
	 * @param businessKey
	 * @param superProcessInstanceId
	 * @return
	 */
	@GET("/api/runtime/findActiveProcessInstance")
	BaseOutput<ProcessInstanceMapping> findActiveProcessInstance(@ReqParam(value = "processInstanceId", required = false) String processInstanceId, @ReqParam(value = "businessKey", required = false) String businessKey, @ReqParam(value = "superProcessInstanceId", required = false) String superProcessInstanceId);

	/**
	 * 根据流程实例id或businessKey查询历史流程实例
	 * 两个参数至少传一个
	 * @param processInstanceId
	 * @param businessKey
	 * @return
	 */
	@GET("/api/runtime/findHistoricProcessInstance")
	BaseOutput<ProcessInstanceMapping> findHistoricProcessInstance(@ReqParam(value = "processInstanceId", required = false) String processInstanceId, @ReqParam(value = "businessKey", required = false) String businessKey);

	/**
	 * 查询历史流程实例列表
	 * @param historicProcessInstanceQueryDto
	 * @return
	 */
	@GET("/api/runtime/listHistoricProcessInstance")
	BaseOutput<ProcessInstanceMapping> listHistoricProcessInstance(@VOBody HistoricProcessInstanceQueryDto historicProcessInstanceQueryDto);

	/**
	 * 根据流程实例id更新businessKey
	 * @param processInstanceId
	 * @param businessKey
	 * @return
	 */
	@POST("/api/runtime/updateBusinessKey")
	@GlobalTx
	BaseOutput updateBusinessKey(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "businessKey") String businessKey);

	/**
	 * 根据key和参数启动流程定义
	 * @param startProcessInstanceDto processDefinitionKey  流程定义key， 必填
	 * @param startProcessInstanceDto businessKey   业务key，选填
	 * @param startProcessInstanceDto userId   用户id，必填
	 * @param startProcessInstanceDto variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@POST("/api/runtime/startProcessInstanceByKey")
	@GlobalTx
	BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@VOBody StartProcessInstanceDto startProcessInstanceDto);

	/**
	 * 根据流程定义id和参数启动流程定义
	 * @param startProcessInstanceDto processDefinitionId 流程定义id, 必填
	 * @param startProcessInstanceDto businessKey   业务key，选填
	 * @param startProcessInstanceDto userId   用户id，必填
	 * @param startProcessInstanceDto variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@POST("/api/runtime/startProcessInstanceById")
	@GlobalTx
	BaseOutput<ProcessInstanceMapping> startProcessInstanceById(@VOBody StartProcessInstanceDto startProcessInstanceDto);

	/**
	 * 结束流程实例
	 * @param processInstanceId	流程实例id
	 * @param deleteReason 结束原因
	 * @return
	 */
	@POST("/api/runtime/stopProcessInstanceById")
	@GlobalTx
	BaseOutput stopProcessInstanceById(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "deleteReason", required = false) String deleteReason);

}
