package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: WangMi
 * @Date: 2019/12/6 16:18
 * @Description:
 */
@FeignClient(name = "dili-bpmc", contextId = "form-service", url="${BpmcRpc.url:}")
public interface FormFeignRpc {

    @GetMapping("/api/form/getByKey")
    BaseOutput<ActForm> getByKey(@RequestParam(value = "formKey") String formKey);
    
    @PostMapping("/api/form/getByKeys")
    BaseOutput<List<ActForm>> getByKeys(@RequestBody List<String> formKeys);
}
