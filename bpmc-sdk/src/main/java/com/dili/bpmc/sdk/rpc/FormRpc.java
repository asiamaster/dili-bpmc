package com.dili.bpmc.sdk.rpc;

import java.util.List;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

/**
 * @Author: WangMi
 * @Date: 2019/12/6 16:18
 * @Description:
 */
@Restful("${bpmc.server.address}")
public interface FormRpc {

    @GET("/api/form/getByKey")
    BaseOutput<ActForm> getByKey(@ReqParam(value = "formKey") String formKey);
    
    @POST("/api/form/getByKeys")
    BaseOutput<List<ActForm>> getByKeys(@VOBody List<String> formKeys);
}
