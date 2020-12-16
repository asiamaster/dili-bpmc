package com.dili.bpmc.sdk.rpc.restful;

import com.dili.bpmc.sdk.domain.HistoricTaskInstanceMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;

import java.util.List;

/**
 * 流程历史远程方法接口
 */
@Restful("${bpmc.server.address}")
public interface HistoryRpc {

    @GET("/api/history/listHistoricTaskInstance")
    BaseOutput<List<HistoricTaskInstanceMapping>> listHistoricTaskInstance(@ReqParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "fillVariablesLocal", required = false) Boolean fillVariablesLocal);

}
