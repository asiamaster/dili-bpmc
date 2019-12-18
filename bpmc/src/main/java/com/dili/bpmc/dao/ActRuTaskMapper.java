package com.dili.bpmc.dao;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;

import java.util.List;

/**
 * @Author: WangMi
 * @Date: 2019/12/18 9:58
 * @Description:
 */
public interface ActRuTaskMapper {

    /**
     * 查询运行时任务列表
     * @return
     */
    List<TaskMapping> list(TaskDto taskDto);
}
