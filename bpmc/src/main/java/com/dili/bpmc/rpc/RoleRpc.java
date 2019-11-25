package com.dili.bpmc.rpc;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.uap.sdk.domain.Role;

import java.util.List;

@Restful("${uap.contextPath}")
public interface RoleRpc {

	@POST("/roleApi/listByUser.api")
	BaseOutput<List<Role>> listByUser(@ReqParam("userId") Long userId, @ReqParam("userName") String userName);


}
