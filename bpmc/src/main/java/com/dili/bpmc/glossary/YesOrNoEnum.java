package com.dili.bpmc.glossary;

/**
 *
 * @author wangmi
 * @createTime 2020/10/27
 */
public enum YesOrNoEnum {

    YES(true, "是"),
    NO(false, "否"),
    ;

    private String name;
    private Boolean code ;

    YesOrNoEnum(Boolean code, String name){
        this.code = code;
        this.name = name;
    }

    public static YesOrNoEnum getYesOrNoEnum(Boolean code) {
        for (YesOrNoEnum anEnum : YesOrNoEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public Boolean getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
