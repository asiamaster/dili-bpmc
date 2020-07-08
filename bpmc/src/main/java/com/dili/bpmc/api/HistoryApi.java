package com.dili.bpmc.api;

import com.dili.bpmc.sdk.domain.HistoricTaskInstanceMapping;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.BeanConver;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程历史接口
 */
@RestController
@RequestMapping("/api/history")
public class HistoryApi {
    private final Logger log = LoggerFactory.getLogger(HistoryApi.class);
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskService taskService;

    /**
     * 根据流程实例id查询历史任务
     * @param processInstanceId
     * @throws Exception
     */
    @RequestMapping(value = "/listHistoricTaskInstance", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<HistoricTaskInstanceMapping>> listHistoricTaskInstance(@RequestParam String processInstanceId, @RequestParam Boolean fillVariablesLocal){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().list();
        List<HistoricTaskInstanceMapping> historicTaskInstanceMappings = new ArrayList<>(list.size());
        if(fillVariablesLocal) {
            list.stream().forEach(t -> {
                HistoricTaskInstanceMapping taskInstanceMapping = BeanConver.copyBean(t, DTOUtils.newInstance(HistoricTaskInstanceMapping.class));
                historicTaskInstanceMappings.add(taskInstanceMapping);
                List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().taskId(t.getId()).list();
                if(historicVariableInstances.isEmpty()){
                    return;
                }
                Map<String, Object> localVariables = new HashMap<>();
                for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
                    localVariables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
                }
                taskInstanceMapping.setTaskLocalVariables(localVariables);
            });
        }else{
            list.stream().forEach(t -> {
                historicTaskInstanceMappings.add(BeanConver.copyBean(t, DTOUtils.newInstance(HistoricTaskInstanceMapping.class)));
            });
        }
        return BaseOutput.successData(historicTaskInstanceMappings);
    }


}
