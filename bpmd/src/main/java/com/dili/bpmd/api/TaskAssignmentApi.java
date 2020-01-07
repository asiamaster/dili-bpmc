package com.dili.bpmd.api;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务分配接口
 */
@RestController
@RequestMapping("/api/taskAssignment")
public class TaskAssignmentApi {

    /**
     * 请假第一步审批对象
     * @param taskMapping 任务代理对象
     * @return
     */
    @RequestMapping(value="/vacationStep1Assignment", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<Assignment> vacationStep1Assignment(TaskMapping taskMapping) {
        Assignment assignment = DTOUtils.newInstance(Assignment.class);
        assignment.setCandidateUser(Lists.newArrayList("1"));
        assignment.setCandidateGroup(Lists.newArrayList("19"));
        return BaseOutput.success().setData(assignment);
    }

    /**
     * 请假第一步审批对象
     * @param taskMapping 任务代理对象
     * @return
     */
    @RequestMapping(value="/vacationStep2Assignment", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<Assignment> vacationStep2Assignment(TaskMapping taskMapping) {
        Assignment assignment = DTOUtils.newInstance(Assignment.class);
        assignment.setCandidateUser(Lists.newArrayList("1"));
        assignment.setCandidateGroup(Lists.newArrayList("19"));
        return BaseOutput.success().setData(assignment);
    }
}
