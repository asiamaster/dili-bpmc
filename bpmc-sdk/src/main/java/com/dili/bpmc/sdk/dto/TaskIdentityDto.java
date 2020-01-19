package com.dili.bpmc.sdk.dto;

import java.util.List;

public interface TaskIdentityDto extends ProcessInstanceDto {

	String getAssignee();

	void setAssignee(String assignee);
	
	String getFormKey();
	
	void setFormKey(String formKey);

	List<GroupUserDto> getGroupUsers();

	void setGroupUsers(List<GroupUserDto> groupUsers);
}
