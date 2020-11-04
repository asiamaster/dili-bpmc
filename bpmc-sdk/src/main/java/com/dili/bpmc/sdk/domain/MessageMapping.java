package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

/**
 * 映射org.activiti.bpmn.model.Message
 *
 * @author: WM
 * @time: 2020/11/4 9:18
 */
public interface MessageMapping extends IDTO {

  /**
   * 消息编号
   * @return
   */
  String getId();

    void setId(String id);

  /**
   * 消息名称
   * @return
   */
  String getName();

    void setName(String name);
}
