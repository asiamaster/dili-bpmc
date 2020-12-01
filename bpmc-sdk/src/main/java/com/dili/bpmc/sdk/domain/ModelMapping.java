package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IStringDomain;

import java.util.Date;

public interface ModelMapping extends IStringDomain {

    @Override
    String getId();
    void setId();

    String getName();

    void setName(String var1);

    String getKey();

    void setKey(String var1);

    String getCategory();

    void setCategory(String category);

    Date getCreateTime();
    void setCreateTime(Date date);

    Date getLastUpdateTime();

    void setLastUpdateTime(Date lastUpdateTime);

    Integer getVersion();

    void setVersion(Integer var1);

    String getMetaInfo();

    void setMetaInfo(String var1);

    String getDeploymentId();

    void setDeploymentId(String var1);

    void setTenantId(String tenantId);

    String getTenantId();

}
