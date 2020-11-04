package com.dili.bpmc.sdk.rpc;

import com.dili.bpmc.sdk.domain.MessageMapping;
import com.dili.bpmc.sdk.domain.ProcessDefinitionMapping;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.SignalMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import org.activiti.bpmn.model.BpmnModel;

import java.util.List;
import java.util.Map;

/**
 * 仓库接口
 */
@Restful("${bpmc.server.address}")
public interface RepositoryRpc {

	/**
	 * 根据流程定义id查询消息定义
	 * @param processDefinitionId
	 * @return
	 */
	@GET("/api/repository/listMessages")
	BaseOutput<List<MessageMapping>> listMessages(@ReqParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询信号定义
	 * @param processDefinitionId
	 * @return
	 */
	@GET("/api/repository/listSignals")
	BaseOutput<List<SignalMapping>> listSignals(@ReqParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询部署信息
	 * @param processDefinitionId
	 * @return
	 */
	@GET("/api/repository/getBpmnModel")
	BaseOutput<BpmnModel> getBpmnModel(@ReqParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询部署信息
	 * @param processDefinitionId
	 * @return
	 */
	@GET("/api/repository/getProcessDefinition")
	BaseOutput<ProcessDefinitionMapping> getProcessDefinition(@ReqParam(value = "processDefinitionId") String processDefinitionId);

}
