package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IDTO;

import java.util.Map;

/**
 * 变量设置DTO基类
 * @description:
 * @author: WM
 * @time: 2020/11/30 17:45
 */
public interface VariablesDto extends IDTO {

    /**
     * 变量
     * @return
     */
    Map<String, Object> getVariables();

    void setVariables(Map<String, Object> variables);
}
