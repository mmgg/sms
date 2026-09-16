package com.clwu.sms.config;

/**
 * MyBatis-Plus 逻辑删除常量，避免在实体中散落魔法数字。
 */
public final class LogicalDeleteConstants {

    public static final String NOT_DELETED = "0";
    public static final String DELETED = "1";

    private LogicalDeleteConstants() {
    }
}
