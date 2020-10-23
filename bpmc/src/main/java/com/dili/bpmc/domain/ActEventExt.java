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
 * 动态业务流事件扩展。html、script、handler_url、resource_code和check_menu非必填，根据实际开发情况决定是否动态配置
 * This file was generated on 2020-10-21 14:36:44.
 */
@Table(name = "`act_event_ext`")
public interface ActEventExt extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`activity_id`")
    @FieldDef(label="活动id", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getActivityId();

    void setActivityId(String activityId);

    @Column(name = "`event_type`")
    @FieldDef(label="事件类型(message和signal)", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEventType();

    void setEventType(String eventType);

    @Column(name = "`event_name`")
    @FieldDef(label="事件名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEventName();

    void setEventName(String eventName);

    @Column(name = "`process_definition_id`")
    @FieldDef(label="流程定义id", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessDefinitionId();

    void setProcessDefinitionId(String processDefinitionId);

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

    @Column(name = "`resource_code`")
    @FieldDef(label="资源编码", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getResourceCode();

    void setResourceCode(String resourceCode);

    @Column(name = "`check_menu`")
    @FieldDef(label="是否检查资源菜单")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getCheckMenu();

    void setCheckMenu(Byte checkMenu);

    @Column(name = "`html`")
    @FieldDef(label="HTML", maxLength = 500)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHtml();

    void setHtml(String html);

    @Column(name = "`script`")
    @FieldDef(label="js脚本", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getScript();

    void setScript(String script);

    @Column(name = "`handler_url`")
    @FieldDef(label="处理URL", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHandlerUrl();

    void setHandlerUrl(String handlerUrl);
}