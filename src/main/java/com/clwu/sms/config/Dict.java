package com.clwu.sms.config;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: wuchunlong
 * @Date: 2025/10/29 11:23
 * @Description:
 **/
public class Dict {
    static public final String STR_EMPTY = "";
    static public final BigInteger BigINT_ZERO = BigInteger.valueOf(0L);
    static public final BigDecimal BigDEC_ZERO = BigDecimal.valueOf(0.0);

    static public final String REST_STR_SUCCESS = "success";
    // 正常
    static public final Integer REST_INT_OK = 200;
    // 参数异常
    static public final Integer REST_INT_ARG_ERR = 400;
    // 内部未定义异常
    static public final Integer REST_INT_ERR = 500;


}
