package com.dili.bpmd.consts;

/**
 * 订单状态
 * @Author: WangMi
 * @Date: 2019/12/4 10:07
 * @Description:
 */
public enum OrderState {
    Create(1,"创建"),
    Submit(2,"已提交"),
    Payed(3,"已缴费"),
    Valid(4,"已生效"),
    Cancel(5,"已取消"),
    Invalid(6,"已作废");

    private Integer code ;

    private String desc;

    OrderState(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (OrderState concurrent : OrderState.values()) {
            if (concurrent.getCode().equals(code)) {
                return concurrent.getDesc();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
