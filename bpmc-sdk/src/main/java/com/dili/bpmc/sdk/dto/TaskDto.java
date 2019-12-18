package com.dili.bpmc.sdk.dto;

import java.util.List;
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

    /**
     * 批量根据流程实例id查询任务id
     * @return
     */
    List<String> getProcessInstanceIds();
    void setProcessInstanceIds(List<String> processInstanceIds);
}