package com.dili.bpmc.domain;

/**
 * 任务查询对象
 */
public interface ProcessInstanceDto extends ProcessDefinitionDto {

    String getProcessInstanceId();
    void setProcessInstanceId(String processInstanceId);


}