package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import java.util.Map;

/**
 * @Author: WangMi
 * @Date: 2019/12/5 14:58
 * @Description:
 */
public interface ProcessInstanceMapping extends IDTO {

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
     * The id of the process definition of the process instance.
     */
    String getProcessDefinitionId();
    void setProcessDefinitionId(String processDefinitionId);

    /**
     * The name of the process definition of the process instance.
     */
    String getProcessDefinitionName();
    void setProcessDefinitionName(String processDefinitionName);

    /**
     * The key of the process definition of the process instance.
     */
    String getProcessDefinitionKey();
    void setProcessDefinitionKey(String processDefinitionKey);

    /**
     * The version of the process definition of the process instance.
     */
    Integer getProcessDefinitionVersion();
    void setProcessDefinitionVersion(Integer processDefinitionVersion);

    /**
     * The deployment id of the process definition of the process instance.
     */
    String getDeploymentId();
    void setDeploymentId(String deploymentId);

    /**
     * The business key of this process instance.
     */
    String getBusinessKey();
    void setBusinessKey(String businessKey);

    /**
     * Returns the process variables if requested in the process instance query
     */
    Map<String, Object> getProcessVariables();
    void setProcessVariables(Map<String, Object> processVariables);

    /**
     * The tenant identifier of this process instance
     */
    String getTenantId();
    void setTenantId(String tenantId);

    /**
     * Returns the name of this process instance.
     */
    String getName();
    void setName(String name);

    /**
     * Returns the description of this process instance.
     */
    String getDescription();

    void setDescription(String description);

    /**
     * Returns the localized name of this process instance.
     */
    String getLocalizedName();

    void setLocalizedName(String localizedName);

    /**
     * Returns the localized description of this process instance.
     */
    String getLocalizedDescription();

    void setLocalizedDescription(String localizedDescription);
}
