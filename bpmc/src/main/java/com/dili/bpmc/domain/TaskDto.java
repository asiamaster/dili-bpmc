package com.dili.bpmc.domain;

/**
 * 任务查询对象
 */
public interface TaskDto extends ProcessInstanceDto {

    String getTaskId();
    void setTaskId(String taskId);

    String getAssignee();
    void setAssignee(String assignee);

}