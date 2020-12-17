package com.dili.bpmc.sdk.rpc.feign;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dili-bpmc", contextId = "bpmcForm-service", url="${BpmcRpc.url:}")
public interface BpmcFormFeignRpc {

	/**
	 * 根据表单key查询ActForm
	 * @param formKey   表单key，必填
	 * @return ActForm
	 */
	@GetMapping(value = "/api/form/getByKey")
    BaseOutput<ActForm> getByKey(@RequestParam(value = "formKey") String formKey);

	/**
	 * 新增ActForm
	 * @param actForm   表单, 必填
	 * @return 影响行数
	 */
	@PostMapping("/api/form/insert")
    BaseOutput<Integer> insert(@RequestBody ActForm actForm);

	/**
	 * 根据key修改ActForm
	 * @param actForm   表单, 必填
	 * @return 影响行数
	 */
	@PostMapping("/api/form/updateByKey")
    BaseOutput<Integer> updateByKey(@RequestBody ActForm actForm);

}
