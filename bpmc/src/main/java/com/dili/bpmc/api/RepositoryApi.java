package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.ProcessDefinitionMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
