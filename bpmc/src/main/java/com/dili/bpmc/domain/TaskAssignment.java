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
 * 
 * This file was generated on 2019-12-25 14:11:30.
 */
@Table(name = "`task_assignment`")
public interface TaskAssignment extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`assignee`")
    @FieldDef(label="办理人", maxLength = 10)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getAssignee();

    void setAssignee(String assignee);

    @Column(name = "`candidate_user`")
    @FieldDef(label="候选人", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCandidateUser();

    void setCandidateUser(String candidateUser);

    @Column(name = "`candidate_group`")
    @FieldDef(label="候选组", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCandidateGroup();

    void setCandidateGroup(String candidateGroup);

    @Column(name = "`handler_url`")
    @FieldDef(label="处理URL", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHandlerUrl();

    void setHandlerUrl(String handlerUrl);

    @Column(name = "`task_definition_key`")
    @FieldDef(label="任务定义key", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTaskDefinitionKey();

    void setTaskDefinitionKey(String taskDefinitionKey);

    @Column(name = "`process_definition_key`")
    @FieldDef(label="流程定义key", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessDefinitionKey();

    void setProcessDefinitionKey(String processDefinitionKey);

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