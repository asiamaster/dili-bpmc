package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import javax.persistence.*;
import java.util.Date;

/**
 * 事件映射
 * 映射org.activiti.engine.impl.persistence.entity.EventSubscriptionEntity
 *
 * @description:
 * @author: WM
 * @time: 2020/10/19 16:39
 */
@Table(
        name = "`ACT_RU_EVENT_SUBSCR`"
)
public interface EventSubscriptionMapping extends IDTO {
    @Column(
            name = "`ID_`"
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    String getId();

    void setId(String id);

    @Column(
            name = "`REV_`"
    )
    Integer getRevision();

    void setRevision(Integer revision);

    @Column(
            name = "`EVENT_TYPE_`"
    )
    String getEventType();

    void setEventType(String eventType);

    @Column(
            name = "`EVENT_NAME_`"
    )
    String getEventName();

    void setEventName(String eventName);

    @Column(
            name = "`EXECUTION_ID_`"
    )
    String getExecutionId();

    void setExecutionId(String executionId);

    @Column(
            name = "`PROC_INST_ID_`"
    )
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    @Column(
            name = "`ACTIVITY_ID_`"
    )
    String getActivityId();

    void setActivityId(String activityId);

    @Column(
            name = "`CONFIGURATION_`"
    )
    String getConfiguration();

    void setConfiguration(String configuration);

    @Column(
            name = "`CREATED_`"
    )
    Date getCreated();

    void setCreated(Date created);

    @Column(
            name = "`PROC_DEF_ID_`"
    )
    String getProcessDefinitionId();

    void setProcessDefinitionId(String processDefinitionId);

    @Column(
            name = "`TENANT_ID_`"
    )
    String getTenantId();

    void setTenantId(String tenantId);
}
