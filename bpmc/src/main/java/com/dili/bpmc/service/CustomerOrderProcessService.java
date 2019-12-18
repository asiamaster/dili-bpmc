package com.dili.bpmc.service;

import com.dili.ss.domain.BaseOutput;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

/**
 * 客户订单流程服务
 */
public interface CustomerOrderProcessService {
    /**
     * 按照流程实例id申领任务并流转到下一节点
     * @param processInstanceId
     */
    void clainAndCompleteTask(String processInstanceId, Map<String, Object> variables);

    /**
     * 抛出消息事件
     * @param messageName
     * @param processInstanceId
     * @param variables
     * @throws IOException
     */
    BaseOutput<String> messageEventReceived(@RequestParam String messageName, String processInstanceId, @RequestParam Map variables);
}
