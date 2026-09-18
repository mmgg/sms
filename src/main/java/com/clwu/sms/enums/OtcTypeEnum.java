package com.clwu.sms.enums;

/**
 * ShowAPI 66-24 返回的药品类型。
 */
public enum OtcTypeEnum {

    OTC(1, "OTC"),
    NON_DRUG(2, "非药品");

    private final int code;
    private final String description;

    OtcTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OtcTypeEnum findByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OtcTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
