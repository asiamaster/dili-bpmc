package com.dili.bpmc.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * 题库选题服务
 * java服务任务调用
 */
@Component
public class TestBankService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("written", "java笔试题");
//        throw new BpmnError("testBankError");
    }
}
