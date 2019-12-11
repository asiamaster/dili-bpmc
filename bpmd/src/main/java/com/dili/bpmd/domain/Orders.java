package com.dili.bpmd.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-12-04 08:59:04.
 */
@Table(name = "`orders`")
public interface Orders extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`code`")
    @FieldDef(label="订单号", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getCode();

    void setCode(String code);

    @Column(name = "`state`")
    @FieldDef(label="订单状态")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getState();

    void setState(Integer state);

    @Column(name = "`effective_time`")
    @FieldDef(label="生效时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEffectiveTime();

    void setEffectiveTime(Date effectiveTime);

    @Column(name = "`dead_time`")
    @FieldDef(label="生效时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getDeadTime();

    void setDeadTime(Date deadTime);

    @Column(name = "`process_instance_id`")
    @FieldDef(label="流程实例id", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    @Column(name = "`process_definition_id`")
    @FieldDef(label="流程定义id", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessDefinitionId();

    void setProcessDefinitionId(String processDefinitionId);

    @Column(name = "`yn`")
    @FieldDef(label="是否有效")
    Boolean getYn();
    void setYn(Boolean Yn);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModified();

    void setModified(Date modified);
}