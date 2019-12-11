package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IBaseDomain;

/**
 * @Author: WangMi
 * @Date: 2019/11/21 16:46
 * @Description:
 */
public interface ProcessDefinitionDto extends IBaseDomain {

    String getProcessDefinitionId();
    void setProcessDefinitionId(String processDefinitionId);
}
