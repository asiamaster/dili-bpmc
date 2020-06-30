package com.dili.bpmc.beetl;

import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.RoleRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Context;
import org.beetl.core.VirtualAttributeEval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务候选人和候选组虚拟属性
 * @Author: WangMi
 * @Date: 2020/6/30 16:29
 * @Description:
 */
@Component
public class TaskCandidateVA implements VirtualAttributeEval {

    @Autowired
    @SuppressWarnings("all")
    private UserRpc userRpc;
    @Autowired
    @SuppressWarnings("all")
    private RoleRpc roleRpc;

    @Autowired
    private TaskService taskService;

    /**
     * 候选人字段
     */
    private final static String CANDIDATE_USER_FIELD = "candidateUser";
    /**
     * 候选组字段
     */
    private final static String CANDIDATE_GROUP_FIELD = "candidateGroup";

    @Override
    public boolean isSupport(Class c, String attributeName) {
        if(!TaskInfo.class.isAssignableFrom(c)){
            return false;
        }
        if(CANDIDATE_USER_FIELD.equals(attributeName) || CANDIDATE_GROUP_FIELD.equals(attributeName)){
            return true;
        }
        return false;
    }

    @Override
    public Object eval(Object o, String attributeName, Context ctx) {
        TaskInfo task = (TaskInfo)o;
        if(task == null){
            return "";
        }
        if(CANDIDATE_USER_FIELD.equals(attributeName)){
            return getCandidateUser(task);
        }else if(CANDIDATE_GROUP_FIELD.equals(attributeName)){
            return getCandidateGroup(task);
        }
        return "";
    }

    /**
     * 获取所有候选人的真实姓名，以逗号分隔
     * @return
     */
    private String getCandidateUser(TaskInfo task){
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        if(CollectionUtils.isEmpty(identityLinksForTask)){
            return "";
        }
        //一般任务候选人不会超过4人
        List<String> ids = new ArrayList<>(4);
        for(IdentityLink identityLink : identityLinksForTask){
            if(identityLink.getUserId() != null) {
                ids.add(identityLink.getUserId());
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        BaseOutput<List<User>> listBaseOutput = userRpc.listUserByIds(ids);
        if(!listBaseOutput.isSuccess()){
            return "";
        }
        for(User user : listBaseOutput.getData()){
            stringBuilder.append(user.getRealName()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length()-2);
    }

    /**
     * 获取所有候选人的真实姓名，以逗号分隔
     * @return
     */
    private String getCandidateGroup(TaskInfo task){
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        if(CollectionUtils.isEmpty(identityLinksForTask)){
            return "";
        }
        //一般任务候选组不会超过4组
        List<String> ids = new ArrayList<>(4);
        for(IdentityLink identityLink : identityLinksForTask){
            if(identityLink.getGroupId() != null) {
                ids.add(identityLink.getGroupId());
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        BaseOutput<List<Role>> listBaseOutput = roleRpc.listRoleByIds(ids);
        if(!listBaseOutput.isSuccess()){
            return "";
        }
        for(Role role : listBaseOutput.getData()){
            stringBuilder.append(role.getRoleName()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length()-2);
    }
}
