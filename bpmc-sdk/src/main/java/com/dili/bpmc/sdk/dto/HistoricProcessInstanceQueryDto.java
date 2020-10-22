package com.dili.bpmc.sdk.dto;

/**
 * 历史任务实例查询对象
 *  @Author: WangMi
 *  @Date: 2019/11/21 15:37
 *  @Description:
 */
public interface HistoricProcessInstanceQueryDto extends ProcessInstanceQueryDto {

    Boolean getFinished();
    void setFinished(Boolean finished);
}
