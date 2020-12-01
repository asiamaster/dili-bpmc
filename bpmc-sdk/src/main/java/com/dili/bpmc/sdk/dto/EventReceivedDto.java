package com.dili.bpmc.sdk.dto;

import com.dili.ss.dto.IDTO;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 事件接收参数, 用于http接口参数
 */
public interface EventReceivedDto extends IDTO {
    /**
     * 流程实例id
     * 在signalEventReceived中选填，此时全局广播信号事件
     * @return
     */
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    /**
     * 变量
     * @return
     */
    Map<String, Object> getVariables();

    void setVariables(Map<String, Object> variables);

    /**
     * 事件名
     * @return
     */
    @NotNull
    String getEventName();

    void setEventName(String eventName);

    /**
     * 执行id,非必填
     * @return
     */
    String getExecutionId();
    void setExecutionId(String executionId);

    /**
     * 事件类型：message、signal和javaReceiveTask
     * @return
     */
    String getEventType();
    void setEventType(String eventType);
}
