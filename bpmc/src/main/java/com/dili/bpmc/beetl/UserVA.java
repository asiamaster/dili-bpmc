package com.dili.bpmc.beetl;

import com.dili.bpmc.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.User;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Context;
import org.beetl.core.VirtualAttributeEval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户虚拟属性
 * @Author: WangMi
 * @Date: 2019/11/27 15:13
 * @Description:
 */
@Component
public class UserVA implements VirtualAttributeEval {

    @Autowired
    private UserRpc userRpc;
    @Override
    public boolean isSupport(Class c, String attributeName) {
        if(!Task.class.isAssignableFrom(c)){
            return false;
        }
        if("assigneeName".equals(attributeName) || "ownerName".equals(attributeName)){
            return true;
        }
        return false;
    }

    @Override
    public Object eval(Object o, String attributeName, Context ctx) {
        Task task = (Task)o;
        if(task==null){
            return "";
        }
        String userId = null;
        if("assigneeName".equals(attributeName)){
            userId = task.getAssignee();
        }else if("ownerName".equals(attributeName)){
            userId = task.getOwner();
        }
        if(StringUtils.isBlank(userId)){
            return "";
        }
        BaseOutput<User> user = userRpc.get(Long.parseLong(userId));
        if(!user.isSuccess()){
            return "";
        }
        return user.getData().getRealName();
    }
}
