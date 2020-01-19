package com.dili.bpmc.sdk.dto;

/**
 * 任务查询对象
 */
public interface ProcessInstanceDto extends ProcessDefinitionDto {

	/**
	 * 流程实例id
	 * 
	 * @return
	 */
	String getProcessInstanceId();

	void setProcessInstanceId(String processInstanceId);

}