package com.dili.bpmc.sdk.dto;

import java.util.Map;

/**
 * 任务查询对象
 */
public interface TaskDto extends ProcessInstanceDto {

    String getTaskId();
    void setTaskId(String taskId);

    String getAssignee();
    void setAssignee(String assignee);

    String getTaskDefinitionKey();
    void setTaskDefinitionKey(String taskDefinitionKey);

    String getCandidateUser();

    void setCandidateUser(String candidateUser);

    Map<String, Object> getProcessVariables();
    void setProcessVariables(Map<String, Object> processVariables);

    Map<String, Object> getTaskVariables();
    void setTaskVariables(Map<String, Object> taskVariables);

    String getProcessInstanceBusinessKey();
    void setProcessInstanceBusinessKey(String processInstanceBusinessKey);
}