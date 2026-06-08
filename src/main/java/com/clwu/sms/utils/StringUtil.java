package com.clwu.sms.utils;

import java.util.regex.Pattern;
/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 14:29
 * @Description:
 **/
public class StringUtil {
    /**
     * 判断字符串是否为 null 或 空字符串("")
     * @param str 输入字符串
     * @return true 如果不为 null 且不为空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 判断字符串是否为 null、空字符串("") 或 全部是空白字符
     * @param str 输入字符串
     * @return true 如果不为 null 且不全是空白
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 判断字符串是否为 null 或 空字符串("")
     * @param str 输入字符串
     * @return true 如果是 null 或 空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否为 null、空字符串("") 或 全部是空白字符
     * @param str 输入字符串
     * @return true 如果是 null 或 空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否是手机号
     * @param phone 字符串
     * @return
     */
    public static boolean isPhoneNum(String phone) {
        final String PHONE_REGEX = "^1[3-9]\\d{9}$";
        final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
