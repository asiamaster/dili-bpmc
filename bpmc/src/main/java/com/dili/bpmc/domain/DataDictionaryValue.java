package com.dili.bpmc.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-05-22 11:25:14.
 */
@Table(name = "`data_dictionary_value`")
public interface DataDictionaryValue extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`parent_id`")
    @FieldDef(label="上级id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getParentId();

    void setParentId(Long parentId);

    @Column(name = "`dd_code`")
    @FieldDef(label="数据字典编码", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDdCode();

    void setDdCode(String ddCode);

    @Column(name = "`order_number`")
    @FieldDef(label="排序号")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getOrderNumber();

    void setOrderNumber(Integer orderNumber);

    @Column(name = "`name`")
    @FieldDef(label="名称", maxLength = 40)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`code`")
    @FieldDef(label="编码", maxLength = 40)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCode();

    void setCode(String code);

    @Column(name = "`description`")
    @FieldDef(label="描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);
}