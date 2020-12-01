package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IDTO;

import java.util.Map;

/**
 * 流程启动变量
 * @author: WM
 * @time: 2020/11/30 17:34
 */
public interface StartProcessInstanceDto extends IDTO {

    /**
     * 流程定义Key
     * @return
     */
    String getProcessDefinitionKey();

    void setProcessDefinitionKey(String processDefinitionKey);

    /**
     * 流程定义ID
     * @return
     */
    String getProcessDefinitionId();

    void setProcessDefinitionId(String processDefinitionId);

    /**
     * 业务编号
     * @return
     */
    String getBusinessKey();
    void setBusinessKey(String businessKey);

    /**
     * 用户id
     * @return
     */
    String getUserId();
    void setUserId(String userId);

    /**
     * 变量
     * @return
     */
    Map<String, Object> getVariables();

    void setVariables(Map<String, Object> variables);
}
