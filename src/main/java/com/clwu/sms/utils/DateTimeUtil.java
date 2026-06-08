package com.clwu.sms.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 14:04
 * @Description: 时间转换类
 **/
public class DateTimeUtil {
    // 默认格式
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    /**
     * String 转 LocalDateTime
     * @param timeStr  时间字符串，如 "2025-09-23 11:30:00"
     * @return LocalDateTime
     */
    public static LocalDateTime parseStringToLocalDateTime(String timeStr) {
        return LocalDateTime.parse(timeStr, DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime 转 String
     * @param localDateTime LocalDateTime
     * @return 格式化后的字符串，如 "2025-09-23 11:30:00"
     */
    public static String formatLocalDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime 转 java.sql.Timestamp (数据库 DATETIME 使用)
     * @param localDateTime LocalDateTime
     * @return java.sql.Timestamp
     */
    public static Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * java.sql.Timestamp 转 LocalDateTime
     * @param timestamp java.sql.Timestamp
     * @return LocalDateTime
     */
    public static LocalDateTime fromSqlTimestamp(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    /**
     * String 转 java.sql.Timestamp
     * @param timeStr 时间字符串
     * @return java.sql.Timestamp
     */
    public static Timestamp parseStringToTimestamp(String timeStr) {
        LocalDateTime ldt = parseStringToLocalDateTime(timeStr);
        return toSqlTimestamp(ldt);
    }

    /**
     * java.sql.Timestamp 转 String
     * @param timestamp java.sql.Timestamp
     * @return 格式化字符串
     */
    public static String formatTimestampToString(Timestamp timestamp) {
        return formatLocalDateTimeToString(fromSqlTimestamp(timestamp));
    }

    /**
     * 获取当前时间的字符串
     * @return 当前时间字符串
     */
    public static String nowString() {
        return formatLocalDateTimeToString(LocalDateTime.now(ZoneId.systemDefault()));
    }

    /**
     * 获取当前时间
     * @return LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

}
