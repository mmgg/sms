package com.clwu.sms.exception;

/**
 * @Author: wuchunlong
 * @Date: 2025/11/17 11:26
 * @Description: 业务异常处理
 **/
public class BusinessException extends RuntimeException{
    private Integer code;
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
