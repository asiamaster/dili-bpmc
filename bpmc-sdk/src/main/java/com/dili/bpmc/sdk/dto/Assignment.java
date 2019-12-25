package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IDTO;

import java.util.List;

/**
 * 用于动态设置任务分配方案
 */
public interface Assignment extends IDTO {
    /**
     * 办理人
     * @return
     */
    String getAssignee();

    void setAssignee(String assignee);

    /**
     * 候选人
     * @return
     */
    List<String> getCandidateUser();

    void setCandidateUser(List<String> candidateUser);

    /**
     * 候选组
     * @return
     */
    List<String> getCandidateGroup();

    void setCandidateGroup(List<String> candidateGroup);

}
