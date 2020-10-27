package com.dili.bpmc.service.impl;

import com.dili.bpmc.dao.ActTaskTitleMapper;
import com.dili.bpmc.domain.ActTaskTitle;
import com.dili.bpmc.service.ActTaskTitleService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-10-27 09:40:40.
 */
@Service
public class ActTaskTitleServiceImpl extends BaseServiceImpl<ActTaskTitle, Long> implements ActTaskTitleService {

    public ActTaskTitleMapper getActualDao() {
        return (ActTaskTitleMapper)getDao();
    }
}