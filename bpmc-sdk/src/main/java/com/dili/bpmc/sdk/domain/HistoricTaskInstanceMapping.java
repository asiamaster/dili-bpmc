package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import java.util.Date;

/**
 * 历史任务实例映射
 */
public interface HistoricTaskInstanceMapping extends TaskInfoMapping, HistoricDataMapping {

    /** The reason why this task was deleted {'completed' | 'deleted' | any other user defined string }. */
    String getDeleteReason();
    void setDeleteReason(String deleteReason);

    /** Time when the task started. */
    Date getStartTime();
    void setStartTime(Date startTime);

    /** Time when the task was deleted or completed. */
    Date getEndTime();
    void setEndTime(Date endTime);

    /** Difference between {@link #getEndTime()} and {@link #getStartTime()} in milliseconds.  */
    Long getDurationInMillis();
    void setDurationInMillis(Long durationInMillis);


    /** Difference between {@link #getEndTime()} and {@link #getClaimTime()} in milliseconds.  */
    Long getWorkTimeInMillis();
    void setWorkTimeInMillis(Long workTimeInMillis);


    /** Time when the task was claimed. */
    Date getClaimTime();
    void setClaimTime(Date claimTime);

    /** Sets an optional localized name for the task. */
    void setLocalizedName(String localizedName);
    String getLocalizedName();

    /** Sets an optional localized description for the task. */
    void setLocalizedDescription(String localizedDescription);
    String getLocalizedDescription();
}
