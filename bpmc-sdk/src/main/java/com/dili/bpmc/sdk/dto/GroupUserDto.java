package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IBaseDomain;

public interface GroupUserDto extends IBaseDomain {

	String getGroupId();

	void setGroupId(String groupId);

	String getUserId();

	void setUserId(String userId);
}
