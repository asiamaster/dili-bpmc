package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IDTO;

public interface GroupUserDto extends IDTO {

	String getGroupId();

	void setGroupId(String groupId);

	String getUserId();

	void setUserId(String userId);
}
