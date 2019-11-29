package com.dili.bpmc.rpc;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.User;

/**
 * 用户接口
 */
@Restful("${uap.contextPath}")
public interface UserRpc {

	/**
	 * 根据id查询用户
	 * @param userId
	 * @return
	 */
	@POST("/userApi/get.api")
	BaseOutput<User> get(@VOBody Long userId);


}
