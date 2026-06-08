package com.clwu.sms.exception;

/**
 * @Author: wuchunlong
 * @Date: 2025/11/17 11:35
 * @Description:
 **/
public class MethodArgumentNotValidException extends RuntimeException{
    private Integer code;
    public MethodArgumentNotValidException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
