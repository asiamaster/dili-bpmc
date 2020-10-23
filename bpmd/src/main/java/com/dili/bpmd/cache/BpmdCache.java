package com.dili.bpmd.cache;

import com.dili.bpmc.sdk.domain.EventSubscriptionMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: WM
 * @time: 2020/10/23 10:21
 */
public class BpmdCache {
    /**
     * 订单事件缓存， key为processInstanceId_state, value为事件名称列表
     */
    public static final Map<String, List<String>> ordersEventCache = new HashMap<>(64);
}
