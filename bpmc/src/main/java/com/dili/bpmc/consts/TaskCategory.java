package com.dili.bpmc.consts;

/**
 * 任务类别
 * @Author: WangMi
 * @Date: 2019/11/22 17:14
 * @Description:
 */
public enum TaskCategory {
    //待办
    INBOX("inbox"),
    //我的任务
    TASKS("tasks"),
    //任务队列
    QUEUED("queued"),
    //受邀
    INVOLVED("involved"),
    //已归档
    ARCHIVED("archived");

    private String code ;

    TaskCategory(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
