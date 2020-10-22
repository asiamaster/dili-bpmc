package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IBaseDomain;

/**
 * 流程实例查询DTO
 */
public interface ProcessInstanceQueryDto extends IBaseDomain {

  String getProcessInstanceId();
  void setProcessInstanceId(String processInstanceId);

  String getProcessDefinitionKey();
  void setProcessDefinitionKey(String processDefinitionKey);

  String getProcessDefinitionId();
  void setProcessDefinitionId(String processDefinitionId);

  String getBusinessKey();
  void setBusinessKey(String businessKey);

  String getSuperProcessInstanceId();
  void setSuperProcessInstanceId(String superProcessInstanceId);

}
