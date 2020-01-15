package com.dili.bpmc.service.impl;

import com.dili.bpmc.dao.ActFormMapper;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.service.ActFormService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2019-03-21 16:02:46.
 */
@Service
public class ActFormServiceImpl extends BaseServiceImpl<ActForm, Long> implements ActFormService {

	public ActFormMapper getActualDao() {
		return (ActFormMapper) getDao();
	}

	@Override
	public ActForm getByKey(String formKey) {
		ActForm actForm = DTOUtils.newInstance(ActForm.class);
		actForm.setFormKey(formKey);
		return getActualDao().selectOne(actForm);
	}

	@Override
	public List<ActForm> getbyKeys(List<String> formKeys) {
		Example example = new Example(ActForm.class);
		example.createCriteria().andIn("formKey", formKeys);
		return this.getActualDao().selectByExample(example);
	}
}