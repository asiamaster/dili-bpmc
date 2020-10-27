package com.dili.bpmc.dto;

import com.dili.bpmc.domain.ActTaskTitle;
import com.dili.ss.domain.annotation.Operator;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

/**
 * @author: WM
 * @time: 2020/10/27 9:11
 */
@Table(name = "`act_task_title`")
public interface ActTaskTitleDto extends ActTaskTitle {

    @Column(name = "`process_definition_id`")
    @Operator(Operator.IN)
    List<String> getProcessDefinistionIds();
    void setProcessDefinistionIds(List<String> processDefinistionIds);
}
