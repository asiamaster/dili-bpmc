package com.dili.bpmc.sdk.dto;

/**
 * 流程变量设置DTO
 * @description:
 * @author: WM
 * @time: 2020/11/30 17:45
 */
public interface ProcessInstanceVariablesDto extends VariablesDto {

    /**
     * 流程实例id
     * @return
     */
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    /**
     * 活动(流程节点)ID
     * @return
     */
    String getActivityId();
    void setActivityId(String activityId);

}
