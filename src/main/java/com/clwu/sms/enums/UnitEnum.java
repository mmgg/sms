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
    PU_TABLET(1, "片"),
    PU_BOX(2, "盒"),
    PU_BOTTLE(3, "瓶"),
    PU_PIECE(4, "件"),
    PU_ML(21, "毫升"),
    PU_L(22, "升"),
    PL_MG(31, "毫克"),
    PL_G(32,"克"),
    PL_KG(33, "千克");
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




}
