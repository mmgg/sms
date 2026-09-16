package com.clwu.sms.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 08:18
 * @Description:
 **/
public enum UnitEnum {
    PU_UNINIT(0, "未初始化"),
    PU_ITEM(1, "个"),
    PU_BOTTLE(2, "瓶"),
    PU_BOX(3, "盒"),
    PU_BAG(4, "袋"),
    PL_MG(11, "毫克"),
    PL_G(12, "克"),
    PL_KG(13, "千克"),
    PU_ML(21, "毫升"),
    PU_L(22, "升");
    private int code;
    private String desc;

    UnitEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnitEnum findByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UnitEnum unit : values()) {
            if (unit.code == code) {
                return unit;
            }
        }
        return null;
    }
}
