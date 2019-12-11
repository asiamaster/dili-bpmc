package com.dili.bpmc.service.impl;

import com.dili.bpmc.dao.ActControlMapper;
import com.dili.bpmc.sdk.domain.ActControl;
import com.dili.bpmc.service.ActControlService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-03-19 17:14:28.
 */
@Service
public class ActControlServiceImpl extends BaseServiceImpl<ActControl, Long> implements ActControlService {

    public ActControlMapper getActualDao() {
        return (ActControlMapper)getDao();
    }
}