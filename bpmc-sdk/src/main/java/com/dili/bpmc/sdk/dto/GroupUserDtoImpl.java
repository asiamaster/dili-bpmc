package com.dili.bpmc.sdk.dto;

public class GroupUserDtoImpl implements GroupUserDto {

	private String groupId;
	private String userId;

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
