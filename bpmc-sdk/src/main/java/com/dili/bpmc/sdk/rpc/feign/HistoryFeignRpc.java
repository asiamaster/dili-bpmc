package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.HistoricTaskInstanceMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 流程历史远程方法接口
 */
@FeignClient(name = "dili-bpmc")
public interface HistoryFeignRpc {

    @GetMapping("/api/history/listHistoricTaskInstance")
    BaseOutput<List<HistoricTaskInstanceMapping>> listHistoricTaskInstance(@RequestParam(value = "processInstanceId") String processInstanceId, @ReqParam(value = "fillVariablesLocal", required = false) Boolean fillVariablesLocal);

}
