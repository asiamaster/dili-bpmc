package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.ExecutionMapping;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.HistoricProcessInstanceQueryDto;
import com.dili.bpmc.sdk.dto.ProcessInstanceVariablesDto;
import com.dili.bpmc.sdk.dto.StartProcessInstanceDto;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 运行时接口
 */
@FeignClient(name = "dili-bpmc", contextId = "runtime-service", url="${BpmcRpc.url:}")
public interface RuntimeFeignRpc {

	/**
	 * 根据实例id查询进行中的执行
	 * @param processInstanceId
	 * @return
	 */
	@GetMapping("/api/runtime/listExecution")
	BaseOutput<List<ExecutionMapping>> listExecution(@RequestParam(value = "processInstanceId") String processInstanceId);

	/**
	 * 获取流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @return
	 */
	@GetMapping("/api/runtime/getVariables")
	BaseOutput<Map<String, Object>> getVariables(@RequestParam(value = "processInstanceId") String processInstanceId, @RequestParam(value = "activityId", required = false) String activityId);

	/**
	 * 设置流程变量
	 * @param setProcessInstanceVariablesDto processInstanceId
	 * @param setProcessInstanceVariablesDto activityId
	 * @param setProcessInstanceVariablesDto variables
	 * @return
	 */
	@PostMapping("/api/runtime/setVariables")
	BaseOutput setVariables(@RequestBody ProcessInstanceVariablesDto setProcessInstanceVariablesDto);

	/**
	 * 设置流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @param key
	 * @param value
	 * @return
	 */
	@GetMapping("/api/runtime/setVariable")
	BaseOutput setVariable(@RequestParam(value = "processInstanceId") String processInstanceId, @RequestParam(value = "activityId") String activityId, @RequestParam(value = "key") String key, @RequestParam(value = "value") String value);

	/**
	 * 删除流程变量
	 * @param processInstanceId
	 * @param activityId
	 * @param key
	 * @return
	 */
	@GetMapping("/api/runtime/removeVariable")
	BaseOutput removeVariable(@RequestParam(value = "processInstanceId") String processInstanceId, @RequestParam(value = "activityId") String activityId, @RequestParam(value = "key") String key);

	/**
	 * 根据流程实例id或businessKey查询进行中的流程实例
	 * 两个参数至少传一个
	 * @param processInstanceId
	 * @param businessKey
	 * @param superProcessInstanceId
	 * @return
	 */
	@GetMapping("/api/runtime/findActiveProcessInstance")
	BaseOutput<ProcessInstanceMapping> findActiveProcessInstance(@RequestParam(value = "processInstanceId", required = false) String processInstanceId, @RequestParam(value = "businessKey", required = false) String businessKey, @RequestParam(value = "superProcessInstanceId", required = false) String superProcessInstanceId);

	/**
	 * 根据流程实例id或businessKey查询历史流程实例
	 * 两个参数至少传一个
	 * @param processInstanceId
	 * @param businessKey
	 * @return
	 */
	@GetMapping("/api/runtime/findHistoricProcessInstance")
	BaseOutput<ProcessInstanceMapping> findHistoricProcessInstance(@RequestParam(value = "processInstanceId", required = false) String processInstanceId, @RequestParam(value = "businessKey", required = false) String businessKey);

	/**
	 * 查询历史流程实例列表
	 * @param historicProcessInstanceQueryDto
	 * @return
	 */
	@GetMapping("/api/runtime/listHistoricProcessInstance")
	BaseOutput<ProcessInstanceMapping> listHistoricProcessInstance(@RequestBody HistoricProcessInstanceQueryDto historicProcessInstanceQueryDto);

	/**
	 * 根据流程实例id更新businessKey
	 * @param processInstanceId
	 * @param businessKey
	 * @return
	 */
	@GetMapping("/api/runtime/updateBusinessKey")
	BaseOutput updateBusinessKey(@RequestParam(value = "processInstanceId") String processInstanceId, @RequestParam(value = "businessKey") String businessKey);

	/**
	 * 根据key和参数启动流程定义
	 * @param startProcessInstanceDto processDefinitionKey  流程定义key， 必填
	 * @param startProcessInstanceDto businessKey   业务key，选填
	 * @param startProcessInstanceDto userId   用户id，必填
	 * @param startProcessInstanceDto variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GetMapping("/api/runtime/startProcessInstanceByKey")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(@RequestBody StartProcessInstanceDto startProcessInstanceDto);

	/**
	 * 根据流程定义id和参数启动流程定义
	 * @param startProcessInstanceDto processDefinitionId 流程定义id, 必填
	 * @param startProcessInstanceDto businessKey   业务key，选填
	 * @param startProcessInstanceDto userId   用户id，必填
	 * @param startProcessInstanceDto variables     启动变量，选填
	 * @return 流程实例对象封装
	 */
	@GetMapping("/api/runtime/startProcessInstanceById")
	BaseOutput<ProcessInstanceMapping> startProcessInstanceById(@RequestBody StartProcessInstanceDto startProcessInstanceDto);

	/**
	 * 结束流程实例
	 * @param processInstanceId	流程实例id
	 * @param deleteReason 结束原因
	 * @return
	 */
	@GetMapping("/api/runtime/stopProcessInstanceById")
	BaseOutput stopProcessInstanceById(@RequestParam(value = "processInstanceId") String processInstanceId, @RequestParam(value = "deleteReason", required = false) String deleteReason);

}
