package com.dili.bpmc.service.impl;

import com.dili.bpmc.service.CustomerOrderProcessService;
import com.dili.ss.domain.BaseOutput;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerOrderProcessServiceImpl implements CustomerOrderProcessService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Override
    public void clainAndCompleteTask(String processInstanceId, Map<String, Object> variables) {
        //根据流程实例id获取任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        //当前用户申领任务
        taskService.claim(task.getId(), "1");
        //完成任务
        taskService.complete(task.getId(), variables);
    }

    @Override
    public BaseOutput<String> messageEventReceived(String messageName, String processInstanceId, Map variables) {
        Execution execution = runtimeService.createExecutionQuery().messageEventSubscriptionName(messageName).processInstanceId(processInstanceId).singleResult();
        runtimeService.messageEventReceived(messageName, execution.getId(), variables);
        return BaseOutput.success("消息发送成功");
    }
}
