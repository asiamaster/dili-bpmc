package com.dili.bpmc.sdk.dto;

/**
 * 任务变量设置DTO
 * @description:
 * @author: WM
 * @time: 2020/11/30 17:45
 */
public interface TaskVariablesDto extends VariablesDto {

    /**
     * 任务id
     * @return
     */
    String getTaskId();

    void setTaskId(String taskId);

}
