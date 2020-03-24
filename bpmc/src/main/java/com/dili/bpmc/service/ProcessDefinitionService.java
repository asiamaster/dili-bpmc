package com.dili.bpmc.service;

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * 流程定义服务
 */
public interface ProcessDefinitionService {

    /**
     * 根据id启动流程
     * @param processDefinitionId
     * @param businessKey
     * @param variables
     * @return
     */
    ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, Map<String, Object> variables);

    /**
     * 根据key启动流程
     * @param processDefinitionKey
     * @param businessKey
     * @param variables
     * @return
     */
    ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables);
}
