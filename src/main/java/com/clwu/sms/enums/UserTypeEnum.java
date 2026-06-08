package com.clwu.sms.enums;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 14:38
 * @Description:
 **/
public enum UserTypeEnum {
    UT_UNDEFINE(0, "未定义"),
    UT_DOCTOR(10, "医生"),
    UT_NURSE(20, "护士"),
    UT_PHARMASIST(30, "药师"),
    UT_OTHER(100, "其他");

    private final int code;
    private final String desc;
    UserTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }

    public static UserTypeEnum findEnumByCode(int code) {
        for (UserTypeEnum e : UserTypeEnum.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
