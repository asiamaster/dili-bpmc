package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.MessageMapping;
import com.dili.bpmc.sdk.domain.ProcessDefinitionMapping;
import com.dili.bpmc.sdk.domain.SignalMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Message;
import org.activiti.bpmn.model.Signal;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 流程部署接口
 * @author: WM
 * @time: 2020/10/21 10:07
 */
@RestController
@RequestMapping("/api/repository")
public class RepositoryApi {
    private final Logger log = LoggerFactory.getLogger(RepositoryApi.class);
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 根据流程定义id查询消息定义
     * @param processDefinitionId
     */
    @RequestMapping(value = "/listMessages", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<MessageMapping>> listMessages(@RequestParam String processDefinitionId){
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            Collection<Message> messages = bpmnModel.getMessages();
            List<MessageMapping> messageMappings = new ArrayList<>();
            messages.forEach(t-> {
                messageMappings.add(DTOUtils.asInstance(t, MessageMapping.class));
            });
            return BaseOutput.successData(messageMappings);
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 根据流程定义id查询信号定义
     * @param processDefinitionId
     */
    @RequestMapping(value = "/listSignals", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<SignalMapping>> listSignals(@RequestParam String processDefinitionId){
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            Collection<Signal> signals = bpmnModel.getSignals();
            List<SignalMapping> signalMappings = new ArrayList<>();
            signalMappings.forEach(t-> {
                signalMappings.add(DTOUtils.asInstance(signals, SignalMapping.class));
            });
            return BaseOutput.successData(signalMappings);
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }

    }

    /**
     * 根据流程定义id查询部署信息
     * @param processDefinitionId
     * @throws Exception
     */
    @RequestMapping(value = "/getBpmnModel", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<BpmnModel> getBpmnModel(@RequestParam String processDefinitionId){
        return BaseOutput.successData(repositoryService.getBpmnModel(processDefinitionId));
    }



    /**
     * 查询流程定义
     * get all BPMN information like additional
     * @param processDefinitionId
     * @throws Exception
     */
    @RequestMapping(value = "/getProcessDefinition", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<ProcessDefinitionMapping> getProcessDefinition(@RequestParam String processDefinitionId){
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        ProcessDefinitionMapping processDefinitionMapping = DTOUtils.asInstance(processDefinition, ProcessDefinitionMapping.class);
        //由于DTO不支持is和has语义，只能手工设置
        processDefinitionMapping.setHasGraphicalNotation(processDefinition.hasGraphicalNotation());
        processDefinitionMapping.setHasStartFormKey(processDefinition.hasStartFormKey());
        processDefinitionMapping.setIsSuspended(processDefinition.isSuspended());
        return BaseOutput.successData(processDefinitionMapping);
    }
}
