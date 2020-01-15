package com.dili.bpmc.api;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.service.ActFormService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

/**
 * 表单接口
 * 
 * @author asiamaster
 * @date 2019-11-29
 * @since 1.0
 */
@RestController
@RequestMapping("/api/form")
public class FormApi {
	private final Logger log = LoggerFactory.getLogger(FormApi.class);

	@Autowired
	private ActFormService actFormService;

	/**
	 * 根据表单key查询ActForm
	 * 
	 * @param formKey 表单key，必填
	 * @return ActForm
	 */
	@RequestMapping(value = "/getByKey", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<ActForm> getByKey(@RequestParam String formKey) {
		return BaseOutput.success().setData(actFormService.getByKey(formKey));
	}

	/**
	 * 批量查询ActForm
	 * 
	 * @param formKey
	 * @return
	 */
	@PostMapping("/getByKeys")
	public BaseOutput<List<ActForm>> getByKeys(@RequestBody(required = false) List<String> formKeys) {
		List<ActForm> list = this.actFormService.getbyKeys(formKeys);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 新增ActForm
	 * 
	 * @param actForm 表单, 必填
	 * @return 影响行数
	 */
	@RequestMapping(value = "/insert", method = { RequestMethod.POST })
	public BaseOutput<Integer> insert(ActForm actForm) {
		return BaseOutput.success().setData(actFormService.insertSelective(actForm));
	}

	/**
	 * 根据key修改ActForm
	 * 
	 * @param actForm 表单, 必填
	 * @return 影响行数
	 */
	@RequestMapping(value = "/updateByKey", method = { RequestMethod.POST })
	public BaseOutput<Integer> updateByKey(ActForm actForm) {
		ActForm condition = DTOUtils.newInstance(ActForm.class);
		condition.setFormKey(actForm.getFormKey());
		return BaseOutput.success().setData(actFormService.updateSelectiveByExample(actForm, condition));
	}

}
