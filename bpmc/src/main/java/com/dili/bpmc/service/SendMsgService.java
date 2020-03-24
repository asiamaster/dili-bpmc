package com.dili.bpmc.service;
import java.util.Date;

import com.dili.bpmc.dao.ActFormMapper;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.ss.dto.DTOUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试java任务
 */
@Service
public class SendMsgService implements JavaDelegate {

    @Autowired
    ActFormMapper actFormMapper;
    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        ActForm actForm = DTOUtils.newInstance(ActForm.class);
        actForm.setId(999L);
        actForm.setFormKey("test");
        actForm.setDefKey("test");
        actForm.setActionUrl("");
        actForm.setTaskUrl("");
        actForm.setRedirectUrl("");
        actForm.setCreated(new Date());
        actForm.setModified(new Date());
        actFormMapper.insert(actForm);
        System.out.println("实例id["+execution.getProcessInstanceId() +"],活动id["+execution.getCurrentActivityId()+"]发消息");
    }
}
