package com.dili.bpmc.domain;

import com.dili.ss.dto.IBaseDomain;

/**
 * 历史变量查询对象
 * @Author: WangMi
 * @Date: 2019/11/21 16:43
 * @Description:
 */
public interface HistoricVariableInstanceQueryDto extends IBaseDomain {

    String getProcessInstanceId();
    void setProcessInstanceId(String processInstanceId);

    String getTaskId();
    void setTaskId(String taskId);

    String getVariableName();
    void setVariableName(String variableName);

}
