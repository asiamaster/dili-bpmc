package com.dili.ss.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class SendMsgService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("实例id["+execution.getProcessInstanceId() +"],活动id["+execution.getCurrentActivityId()+"]发消息");
    }
}
