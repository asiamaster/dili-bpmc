package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;

import java.util.Date;

/**
 * HistoricData的本地映射
 */
public interface HistoricDataMapping extends IDTO {
    Date getTime();
    void setTime(Date date);
}
