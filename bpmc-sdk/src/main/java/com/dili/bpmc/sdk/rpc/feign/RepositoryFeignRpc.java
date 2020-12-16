package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.MessageMapping;
import com.dili.bpmc.sdk.domain.ProcessDefinitionMapping;
import com.dili.bpmc.sdk.domain.SignalMapping;
import com.dili.ss.domain.BaseOutput;
import org.activiti.bpmn.model.BpmnModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 仓库接口
 */
@FeignClient(name = "dili-bpmc")
public interface RepositoryFeignRpc {

	/**
	 * 根据流程定义id查询消息定义
	 * @param processDefinitionId
	 * @return
	 */
	@GetMapping("/api/repository/listMessages")
	BaseOutput<List<MessageMapping>> listMessages(@RequestParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询信号定义
	 * @param processDefinitionId
	 * @return
	 */
	@GetMapping("/api/repository/listSignals")
	BaseOutput<List<SignalMapping>> listSignals(@RequestParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询部署信息
	 * @param processDefinitionId
	 * @return
	 */
	@GetMapping("/api/repository/getBpmnModel")
	BaseOutput<BpmnModel> getBpmnModel(@RequestParam(value = "processDefinitionId") String processDefinitionId);

	/**
	 * 根据流程定义id查询部署信息
	 * @param processDefinitionId
	 * @return
	 */
	@GetMapping("/api/repository/getProcessDefinition")
	BaseOutput<ProcessDefinitionMapping> getProcessDefinition(@RequestParam(value = "processDefinitionId") String processDefinitionId);

}
