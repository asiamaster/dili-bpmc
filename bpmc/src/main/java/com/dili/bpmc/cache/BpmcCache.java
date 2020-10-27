package com.dili.bpmc.cache;

import com.dili.bpmc.domain.ActTaskTitle;

import java.util.HashMap;
import java.util.Map;

/**
 * bpmc缓存
 * @author: WM
 * @time: 2020/10/26 16:26
 */
public class BpmcCache {
    /**
     * 任务中心 -> 任务标题缓存（key为taskId， value为标题）
     */
    public static final Map<String, String> TASK_TITLE = new HashMap<>();

    /**
     * 任务中心 -> 任务标题的模板缓存(key为processDefinitionId, value为任务标题)
     */
    public static final Map<String, ActTaskTitle> PROCESS_DEFINITION_TASK_TITLE = new HashMap<>();


}
