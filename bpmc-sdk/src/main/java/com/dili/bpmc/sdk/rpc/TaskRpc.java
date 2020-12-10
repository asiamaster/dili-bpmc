package com.dili.bpmc.sdk.rpc;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskCompleteDto;
import com.dili.bpmc.sdk.dto.TaskVariablesDto;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.*;
import com.dili.ss.seata.annotation.GlobalTx;

import java.util.List;
import java.util.Map;

/**
 * 任务接口
 */
@Restful("${bpmc.server.address}")
public interface TaskRpc {

	/**
	 * 申领(签收)任务
	 * 
	 * @param userId 申领人
	 * @param taskId 任务id 必填
	 */
	@GET("/api/task/claim")
	@GlobalTx
	BaseOutput<String> claim(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "userId") String userId);

	/**
	 * 完成任务(无参)
	 * 
	 * @param taskId 任务id 必填
	 */
	@POST("/api/task/complete")
	@GlobalTx
	BaseOutput<String> complete(@ReqParam(value = "taskId") String taskId);

	/**
	 * 完成任务
	 * 
	 * @param taskCompleteDto taskId    任务id 必填
	 * @param taskCompleteDto assignee  强制插手认领人
	 * @param taskCompleteDto variables
	 * @return 任务id
	 */
	@POST("/api/task/complete")
	@GlobalTx
	BaseOutput<String> complete(@VOBody TaskCompleteDto taskCompleteDto);

	/**
	 * 强制提交任务，使用于无办理人的场景
	 * 
	 * @param taskCompleteDto taskId    必填
	 * @param taskCompleteDto variables
	 */
	@POST("/api/task/completeByForce")
	@GlobalTx
	BaseOutput<String> completeByForce(@VOBody TaskCompleteDto taskCompleteDto);

	/**
	 * 签收并完成任务(无参)
	 * 
	 * @param taskId   任务id 必填
	 * @param assignee 强制插手认领人
	 */
	@POST("/api/task/complete")
	@GlobalTx
	BaseOutput<String> complete(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "assignee", required = false) String assignee);

	/**
	 * 获取任务变量
	 * 
	 * @param taskId，必填
	 * @return
	 */
	@POST("/api/task/getVariables")
	BaseOutput<Map<String, Object>> getVariables(@ReqParam(value = "taskId") String taskId);

	/**
	 * 获取任务变量
	 * 
	 * @param taskId，必填
	 * @param variableName 变量名，必填
	 * @return
	 */
	@POST("/api/task/getVariable")
	BaseOutput<Object> getVariable(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "variableName") String variableName);

	/**
	 * 设置本地任务变量
	 * 
	 * @param setTaskVariablesDto taskId
	 * @param setTaskVariablesDto variables
	 * @return
	 */
	@POST("/api/task/setVariablesLocal")
	@GlobalTx
	BaseOutput setVariablesLocal(@VOBody TaskVariablesDto setTaskVariablesDto);

	/**
	 * 根据任务id查询任务
	 * 
	 * @param taskId，必填
	 * @return
	 */
	@GET("/api/task/getById")
	BaseOutput<TaskMapping> getById(@ReqParam(value = "taskId") String taskId);

	/**
	 * 查询任务列表
	 * 
	 * @param taskDto
	 * @return
	 */
	@POST("/api/task/list")
	BaseOutput<List<TaskMapping>> list(@VOBody TaskDto taskDto);

	/**
	 * 使用mybatis自定义任务查询,目前用于根据流程实例ids批量查询任务
	 * 
	 * @param taskDto 任务查询对象
	 * @return
	 * @throws Exception
	 */
	@POST("/api/task/listTaskMapping")
	BaseOutput<List<TaskMapping>> listTaskMapping(@VOBody TaskDto taskDto);

	/**
	 * 根据taksId查询任务候选人、候选组、办理人
	 * 
	 * @param processInstanceIds
	 * @return
	 */
	@POST("/api/task/listTaskIdentityByProcessInstanceIds")
	BaseOutput<List<TaskIdentityDto>> listTaskIdentityByProcessInstanceIds(@VOBody List<String> processInstanceIds);

	/**
	 * 查询用户任务
	 * @param userId	用户id
	 * @param processDefinitionKey	流程定义key
	 * @return
	 */
	@POST("/api/task/listUserTask")
	BaseOutput<List<TaskMapping>> listUserTask(@ReqParam("userId") Long userId, @ReqParam(value = "processDefinitionKey") String processDefinitionKey);

	/**
	 * 触发Java接收任务
	 *
	 * @param processInstanceId
	 * @param activityId
	 * @param variables
	 * @return
	 */
	@Deprecated
	@POST("/api/task/signal")
	BaseOutput<String> signal(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId") String activityId,
							  @ReqParam(value = "variables", required = false) Map<String, String> variables);

}
