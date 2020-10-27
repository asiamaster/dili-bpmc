package com.dili.bpmc.domain;

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
 * 任务标题设置
 * This file was generated on 2020-10-27 09:40:40.
 */
@Table(name = "`act_task_title`")
public interface ActTaskTitle extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`title`")
    @FieldDef(label="任务标题HTML模板", maxLength = 10)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTitle();

    void setTitle(String title);

    @Column(name = "`process_definition_id`")
    @FieldDef(label="流程定义id", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessDefinitionId();

    void setProcessDefinitionId(String processDefinitionId);

    @Column(name = "`refresh`")
    @FieldDef(label="是否实时更新流程数据")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getRefresh();

    void setRefresh(Boolean refresh);

    /**
     * 有的业务，任务标题不需要使用流程变量，可以提升性能
     * @return
     */
    @Column(name = "`load_proc_var`")
    @FieldDef(label="是否加载流程变量")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getLoadProcVar();

    void setLoadProcVar(Boolean loadProcVar);

    @Column(name = "`proc_var`")
    @FieldDef(label="流程变量json", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcVar();

    void setProcVar(String procVar);

    @Column(name = "`creater_id`")
    @FieldDef(label="创建人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCreaterId();

    void setCreaterId(Long createrId);

    @Column(name = "`modifier_id`")
    @FieldDef(label="修改人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getModifierId();

    void setModifierId(Long modifierId);

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