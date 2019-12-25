package com.dili.bpmc.service.impl;

import com.dili.bpmc.dao.TaskAssignmentMapper;
import com.dili.bpmc.domain.TaskAssignment;
import com.dili.bpmc.service.TaskAssignmentService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-25 14:11:30.
 */
@Service
public class TaskAssignmentServiceImpl extends BaseServiceImpl<TaskAssignment, Long> implements TaskAssignmentService {

    public TaskAssignmentMapper getActualDao() {
        return (TaskAssignmentMapper)getDao();
    }
}