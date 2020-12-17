package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskCompleteDto;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.bpmc.sdk.dto.TaskVariablesDto;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 任务接口
 */
@FeignClient(name = "dili-bpmc", contextId = "task-service")
public interface TaskFeignRpc {

	/**
	 * 申领(签收)任务
	 * 
	 * @param userId 申领人
	 * @param taskId 任务id 必填
	 */
	@GetMapping("/api/task/claim")
	BaseOutput<String> claim(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "userId") String userId);

	/**
	 * 完成任务(无参)
	 * 
	 * @param taskId 任务id 必填
	 */
	@PostMapping("/api/task/complete")
	BaseOutput<String> complete(@RequestParam(value = "taskId") String taskId);

	/**
	 * 完成任务
	 * 
	 * @param taskCompleteDto taskId    任务id 必填
	 * @param taskCompleteDto assignee  强制插手认领人
	 * @param taskCompleteDto variables
	 * @return 任务id
	 */
	@PostMapping("/api/task/complete")
	BaseOutput<String> complete(@RequestBody TaskCompleteDto taskCompleteDto);

	/**
	 * 强制提交任务，使用于无办理人的场景
	 * 
	 * @param taskCompleteDto taskId    必填
	 * @param taskCompleteDto variables
	 */
	@PostMapping("/api/task/completeByForce")
	BaseOutput<String> completeByForce(@RequestBody TaskCompleteDto taskCompleteDto);

	/**
	 * 签收并完成任务(无参)
	 * 
	 * @param taskId   任务id 必填
	 * @param assignee 强制插手认领人
	 */
	@PostMapping("/api/task/complete")
	BaseOutput<String> complete(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "assignee", required = false) String assignee);

	/**
	 * 获取任务变量
	 * 
	 * @param taskId，必填
	 * @return
	 */
	@PostMapping("/api/task/getVariables")
	BaseOutput<Map<String, Object>> getVariables(@RequestParam(value = "taskId") String taskId);

	/**
	 * 获取任务变量
	 * 
	 * @param taskId，必填
	 * @param variableName 变量名，必填
	 * @return
	 */
	@PostMapping("/api/task/getVariable")
	BaseOutput<Object> getVariable(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "variableName") String variableName);

	/**
	 * 设置本地任务变量
	 * 
	 * @param setTaskVariablesDto taskId
	 * @param setTaskVariablesDto variables
	 * @return
	 */
	@PostMapping("/api/task/setVariablesLocal")
	BaseOutput setVariablesLocal(@RequestBody TaskVariablesDto setTaskVariablesDto);

	/**
	 * 根据任务id查询任务
	 * 
	 * @param taskId，必填
	 * @return
	 */
	@GetMapping("/api/task/getById")
	BaseOutput<TaskMapping> getById(@RequestParam(value = "taskId") String taskId);

	/**
	 * 查询任务列表
	 * 
	 * @param taskDto
	 * @return
	 */
	@PostMapping("/api/task/list")
	BaseOutput<List<TaskMapping>> list(@RequestBody TaskDto taskDto);

	/**
	 * 使用mybatis自定义任务查询,目前用于根据流程实例ids批量查询任务
	 * 
	 * @param taskDto 任务查询对象
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/api/task/listTaskMapping")
	BaseOutput<List<TaskMapping>> listTaskMapping(@RequestBody TaskDto taskDto);

	/**
	 * 根据taksId查询任务候选人、候选组、办理人
	 * 
	 * @param processInstanceIds
	 * @return
	 */
	@PostMapping("/api/task/listTaskIdentityByProcessInstanceIds")
	BaseOutput<List<TaskIdentityDto>> listTaskIdentityByProcessInstanceIds(@RequestBody List<String> processInstanceIds);

	/**
	 * 查询用户任务
	 * @param userId	用户id
	 * @param processDefinitionKey	流程定义key
	 * @return
	 */
	@PostMapping("/api/task/listUserTask")
	BaseOutput<List<TaskMapping>> listUserTask(@RequestParam("userId") Long userId, @RequestParam(value = "processDefinitionKey") String processDefinitionKey);

}
