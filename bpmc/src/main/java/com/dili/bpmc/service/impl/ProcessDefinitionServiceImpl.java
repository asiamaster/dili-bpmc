package com.dili.bpmc.service.impl;

import com.dili.bpmc.service.ProcessDefinitionService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.http.client.utils.CloneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 流程定义服务
 */
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    //当前节点的出口线key(prepare方法的返回Map的key)
    public static final String OUTGOING_TRANSITIONS_KEY = "outgoingTransitions";
    //当前节点key(prepare方法的返回Map的key)
    public static final String CURRENT_ACTIVITY_KEY = "currentActivity";

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;

    @Override
    @Transactional
    public ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    /**
     * 通过指定目标节点，实现任务的跳转
     *
     * @param taskId      任务ID
     * @param destNodeIds 跳至的目标节点ID
     * @param vars        流程变量
     */
    public synchronized void completeTask(String taskId, String[] destNodeIds, Map<String, Object> vars) {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();

        String curNodeId = task.getTaskDefinitionKey();
        String actDefId = task.getProcessDefinitionId();

        Map<String, Object> activityMap = prepare(actDefId, curNodeId, destNodeIds);
        try {
            taskService.complete(taskId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            //恢复
            // restore(activityMap);
        }
    }

    /**
     * 将节点之后的节点删除然后指向新的节点。
     *
     * @param actDefId       流程定义ID
     * @param nodeId         流程节点ID
     * @param destinations 需要跳转的节点
     * @return Map<String, Object> 返回节点和需要恢复节点的集合。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> prepare(String actDefId, String nodeId, String[] destinations) {
        Map<String, Object> map = new HashMap<>(4);
        //修改流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);
        //获取节点
        ActivityImpl activity = processDefinition.findActivity(nodeId);
        //获取出口线
        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
        try {
            List<PvmTransition> outgoingTransitionsClone = (List<PvmTransition>) CloneUtils.cloneObject(outgoingTransitions);
            map.put(OUTGOING_TRANSITIONS_KEY, outgoingTransitionsClone);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /**
         * 解决通过选择自由跳转指向同步节点导致的流程终止的问题。
         * 在目标节点中删除指向自己的流转。
         */for (Iterator<PvmTransition> it = outgoingTransitions.iterator(); it.hasNext(); ) {
            PvmTransition transition = it.next();
            PvmActivity destination = transition.getDestination();
            List<PvmTransition> incomingTransitions = destination.getIncomingTransitions();
            for (Iterator<PvmTransition> iterator = incomingTransitions.iterator(); iterator.hasNext(); ) {
                PvmTransition inTransition = iterator.next();
                if (inTransition.getSource().getId().equals(activity.getId())) {
                    iterator.remove();
                }
            }
        }
        //删除当前节点的出口线
        activity.getOutgoingTransitions().clear();
         //设置到目标节点的线
        if (destinations != null && destinations.length > 0) {
            for (String dest : destinations) {
                //创建一个连接
                ActivityImpl destAct = processDefinition.findActivity(dest);
                TransitionImpl transitionImpl = activity.createOutgoingTransition();
                transitionImpl.setDestination(destAct);
            }
        }
        map.put(CURRENT_ACTIVITY_KEY, activity);
        return map;
    }

    /**
     * 将临时节点清除掉，加回原来的节点。
     *
     * @param map void
     */
    @SuppressWarnings("unchecked")
    private void restore(Map<String, Object> map) {
        ActivityImpl curAct = (ActivityImpl) map.get(CURRENT_ACTIVITY_KEY);
        List<PvmTransition> outTrans = (List<PvmTransition>) map.get(OUTGOING_TRANSITIONS_KEY);
        curAct.getOutgoingTransitions().clear();
        curAct.getOutgoingTransitions().addAll(outTrans);
    }

}
