package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.dto.IDTO;

/**
 * @Author: WangMi
 * @Date: 2019/11/21 16:46
 * @Description:
 */
public interface ProcessDefinitionDto extends IBaseDomain {

	/**
	 * 流程定义id
	 * 
	 * @return
	 */
	String getProcessDefinitionId();

	void setProcessDefinitionId(String processDefinitionId);
}
