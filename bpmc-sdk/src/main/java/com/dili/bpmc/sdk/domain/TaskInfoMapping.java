package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import java.util.Date;
import java.util.Map;

/**
 * TaskInfo的本地映射
 */
public interface TaskInfoMapping extends IDTO {
    /** DB id of the task. */
    String getId();
    void setId(String id);

    /**
     * Name or title of the task.
     */
    String getName();
    void setName(String name);

    /**
     * Free text description of the task.
     */
    String getDescription();
    void setDescription(String description);

    /**
     * Indication of how important/urgent this task is
     */
    int getPriority();
    void setPriority(int priority);

    /**
     * The {@link User.getId() userId} of the person that is responsible for this
     * task.
     */
    String getOwner();
    void setOwner(String owner);

    /**
     * The {@link User.getId() userId} of the person to which this task is
     * delegated.
     */
    String getAssignee();
    void setAssignee(String assignee);

    /**
     * Reference to the process instance or null if it is not related to a process
     * instance.
     */
    String getProcessInstanceId();
    void setProcessInstanceId(String processInstanceId);

    /**
     * Reference to the path of execution or null if it is not related to a
     * process instance.
     */
    String getExecutionId();
    void setExecutionId(String executionId);

    /**
     * Reference to the process definition or null if it is not related to a
     * process.
     */
    String getProcessDefinitionId();
    void setProcessDefinitionId(String processDefinitionId);

    /** The date/time when this task was created */
    Date getCreateTime();
    void setCreateTime(Date createTime);

    /**
     * The id of the activity in the process defining this task or null if this is
     * not related to a process
     */
    String getTaskDefinitionKey();
    void setTaskDefinitionKey(String taskDefinitionKey);

    /**
     * Due date of the task.
     */
    Date getDueDate();
    void setDueDate(Date dueDate);

    /**
     * The category of the task. This is an optional field and allows to 'tag'
     * tasks as belonging to a certain category.
     */
    String getCategory();
    void setCategory(String category);

    /**
     * The parent task for which this task is a subtask
     */
    String getParentTaskId();
    void setParentTaskId(String parentTaskId);

    /**
     * The tenant identifier of this task
     */
    String getTenantId();
    void setTenantId(String tenantId);

    /**
     * The form key for the user task
     */
    String getFormKey();
    void setFormKey(String formKey);

    /**
     * Returns the local task variables if requested in the task query
     */
    Map<String, Object> getTaskLocalVariables();
    void setTaskLocalVariables(Map<String, Object> taskLocalVariables);

    /**
     * Returns the process variables if requested in the task query
     */
    Map<String, Object> getProcessVariables();
    void setProcessVariables(Map<String, Object> processVariables);
}
