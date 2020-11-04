package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

/**
 * 映射org.activiti.bpmn.model.Signal
 * @author: WM
 * @time: 2020/11/4 9:21
 */
public interface SignalMapping extends IDTO {

    /**
     * 编号
     *
     * @return
     */
    String getId();

    void setId(String id);

    /**
     * 名称
     *
     * @return
     */
    String getName();

    void setName(String name);

    /**
     * 范围（全局/流程初始化）
     *
     * @return
     */
    String getScope();

    void setScope(String scope);
}
