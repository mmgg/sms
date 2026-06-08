package com.clwu.sms.enums;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 08:30
 * @Description:
 **/
public enum PhysicTypeEnum {
    PT_UNINIT(0, "未初始化"),
    PT_PHYSIC(10, "药品"),
    PT_MATERICAL(20, "耗材");

    int code;
    String desc;
    PhysicTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
