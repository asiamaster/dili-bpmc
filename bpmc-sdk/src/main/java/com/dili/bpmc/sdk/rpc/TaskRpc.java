package com.dili.bpmc.sdk.rpc;

import java.util.List;
import java.util.Map;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import org.springframework.web.bind.annotation.RequestParam;

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
	BaseOutput<String> claim(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "userId") String userId);

	/**
	 * 完成任务(无参)
	 * 
	 * @param taskId 任务id 必填
	 */
	@POST("/api/task/complete")
	BaseOutput<String> complete(@ReqParam(value = "taskId") String taskId);

	/**
	 * 完成任务
	 * 
	 * @param taskId    任务id 必填
	 * @param variables
	 * @return 任务id
	 */
	@POST("/api/task/complete")
	BaseOutput<String> complete(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "variables", required = false) Map<String, String> variables);

	/**
	 * 强制提交任务，使用于无办理人的场景
	 * 
	 * @param taskId    必填
	 * @param variables
	 */
	@POST("/api/task/completeByForce")
	BaseOutput<String> completeByForce(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "variables", required = false) Map<String, Object> variables);

	/**
	 * 签收并完成任务(无参)
	 * 
	 * @param taskId   任务id 必填
	 * @param assignee 强制插手认领人
	 */
	@POST("/api/task/complete")
	BaseOutput<String> complete(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "assignee", required = false) String assignee);

	/**
	 * 抛出消息事件
	 * 
	 * @param messageName       必填
	 * @param processInstanceId 必填
	 * @param variables
	 */
	@POST("/api/task/messageEventReceived")
	BaseOutput<String> messageEventReceived(@ReqParam(value = "messageName") String messageName, @ReqParam(value = "processInstanceId") String processInstanceId,
			@ReqParam(value = "variables", required = false) Map<String, Object> variables);

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
	 * @param taskId
	 * @param variables
	 * @return
	 */
	@POST("/api/task/setVariablesLocal")
	BaseOutput setVariablesLocal(@ReqParam(value = "taskId") String taskId, @ReqParam(value = "variables") Map<String, String> variables);

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
	 * 触发Java接收任务
	 * @param processInstanceId
	 * @param activityId
	 * @param variables
	 * @return
	 */
	@POST("/api/task/signal")
	BaseOutput<String> signal(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "activityId") String activityId, @ReqParam(value = "variables", required = false) Map<String, String> variables);


}
