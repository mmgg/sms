package com.clwu.sms.enums;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 13:49
 * @Description: 状态枚举变量
 **/
public enum StatusEnum {
    US_INIT(0, "初始化"),
    US_ENABLED(1, "可用"),
    US_DISABLE(-1, "未启用"),
    US_OCCUPY(2, "被占用");
    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
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
