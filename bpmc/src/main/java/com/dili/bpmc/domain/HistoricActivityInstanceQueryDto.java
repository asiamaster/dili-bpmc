package com.dili.bpmc.domain;

/**
 *  历史活动查询对象
 */
public interface HistoricActivityInstanceQueryDto extends TaskDto {

  String getActivityId();

  void setActivityId(String activityId);

  String getActivityName();

  void setActivityName(String activityName);

}
