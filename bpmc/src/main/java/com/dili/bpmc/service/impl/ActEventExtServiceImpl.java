package com.dili.bpmc.service.impl;

import com.dili.bpmc.dao.ActEventExtMapper;
import com.dili.bpmc.domain.ActEventExt;
import com.dili.bpmc.service.ActEventExtService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-10-21 14:36:44.
 */
@Service
public class ActEventExtServiceImpl extends BaseServiceImpl<ActEventExt, Long> implements ActEventExtService {

    public ActEventExtMapper getActualDao() {
        return (ActEventExtMapper)getDao();
    }
}