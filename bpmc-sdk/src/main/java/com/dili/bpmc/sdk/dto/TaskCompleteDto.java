package com.dili.bpmc.sdk.dto;

/**
 * 任务完成变量
 * @description:
 * @author: WM
 * @time: 2020/11/30 17:45
 */
public interface TaskCompleteDto extends TaskVariablesDto {

    /**
     * 任务办理人id
     * @return
     */
    String getAssignee();

    void setAssignee(String assignee);

}
