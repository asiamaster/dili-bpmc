package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import java.util.Date;
import java.util.Map;

/**
 * @Author: WangMi
 * @Date: 2019/12/5 15:01
 * @Description:
 */
public interface TaskMapping extends IDTO {

    /**
     * 任务id
     * @return
     */
    String getId();
    void setId(String id);
    /**
     * Name or title of the task.
     */
    String getName();

    /** Name or title of the task. */
    void setName(String name);

    /**
     * Free text description of the task.
     */
    String getDescription();

    /** Change the description of the task */
    void setDescription(String description);

    /**
     * Indication of how important/urgent this task is
     */
    int getPriority();

    /** Sets the indication of how important/urgent this task is */
    void setPriority(int priority);

    /**
     * The {@link User.getId() userId} of the person that is responsible for this
     * task.
     */
    String getOwner();

    /** The {@link User.getId() userId} of the person that is responsible for this task. */
    void setOwner(String owner);

    /**
     * The {@link User.getId() userId} of the person to which this task is
     * delegated.
     */
    String getAssignee();

    /** The {@link User.getId() userId} of the person to which this task is delegated. */
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

    /** Change due date of the task. */
    void setDueDate(Date dueDate);

    /**
     * The category of the task. This is an optional field and allows to 'tag'
     * tasks as belonging to a certain category.
     */
    String getCategory();

    /** Change the category of the task. This is an optional field and allows to 'tag' tasks as belonging to a certain category. */
    void setCategory(String category);

    /**
     * The parent task for which this task is a subtask
     */
    String getParentTaskId();

    /** the parent task for which this task is a subtask */
    void setParentTaskId(String parentTaskId);

    /**
     * The tenant identifier of this task
     */
    String getTenantId();

    /** Change the tenantId of the task */
    void setTenantId(String tenantId);

    /**
     * The form key for the user task
     */
    String getFormKey();

    /** Change the form key of the task */
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


    /** Sets an optional localized name for the task. */
    void setLocalizedName(String localizedName);
    String getlocalizedName();

    /** Sets an optional localized description for the task. */
    void setLocalizedDescription(String localizedDescription);
    String getLocalizedDescription();

//    /** The current {@link DelegationState} for this task. */
//    DelegationState getDelegationState();
//
//    /** The current {@link DelegationState} for this task. */
//    void setDelegationState(DelegationState delegationState);

    /**
     * 代理人
     * @param delegation
     */
    void setDelegation(String delegation);
    String getDelegation();

    /**
     * 办理人名字
     */
    String getAssigneeName();

    void setAssigneeName(String assigneeName);
}
