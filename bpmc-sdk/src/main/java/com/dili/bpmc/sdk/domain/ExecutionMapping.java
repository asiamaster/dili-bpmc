package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

/**
 * 映射org.activiti.engine.runtime.Execution
 * @author: WM
 * @time: 2020/11/4 15:38
 */
public interface ExecutionMapping extends IDTO {
    /**
     * The unique identifier of the execution.
     */
    String getId();
    void setId(String id);

    /**
     * Indicates if the execution is suspended.
     */
    Boolean getSuspended();
    void setSuspended(Boolean suspended);

    /**
     * Indicates if the execution is ended.
     */
    Boolean getEnded();
    void setEnded(Boolean ended);

    /**
     * Returns the id of the activity where the execution currently is at.
     * Returns null if the execution is not a 'leaf' execution (eg concurrent parent).
     */
    String getActivityId();
    void setActivityId(String activityId);

    /** Id of the root of the execution tree representing the process instance.
     * It is the same as {@link #getId()} if this execution is the process instance. */
    String getProcessInstanceId();
    void setProcessInstanceId(String processInstanceId);

    /**
     * Gets the id of the parent of this execution. If null, the execution represents a process-instance.
     */
    String getParentId();
    void setParentId(String parentId);

    /**
     * Gets the id of the super execution of this execution.
     */
    String getSuperExecutionId();
    void setSuperExecutionId(String superExecutionId);


    /**
     * The tenant identifier of this process instance
     */
    String getTenantId();
    void setTenantId(String tenantId);

    /**
     * Returns the name of this execution.
     */
    String getName();
    void setName(String name);

    Boolean getActive();
    void setActive(Boolean active);

}
