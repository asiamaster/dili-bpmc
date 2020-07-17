package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import javax.validation.constraints.NotNull;

/**
 * 任务中心打开任务页面传的参数
 */
public interface TaskCenterParam extends IDTO {
    @NotNull(message = "任务id不能为空")
    String getTaskId();

    void setTaskId(String taskId);

    @NotNull(message = "表单KEY不能为空")
    String getFormKey();

    void setFormKey(String formKey);

    @NotNull(message = "任务定义KEY不能为空")
    String getTaskDefinitionKey();

    void setTaskDefinitionKey(String taskDefinitionKey);

    @NotNull(message = "流程实例id不能为空")
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    String getBusinessKey();

    void setBusinessKey(String businessKey);
}
