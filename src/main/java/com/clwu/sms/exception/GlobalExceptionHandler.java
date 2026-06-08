package com.clwu.sms.exception;

import com.clwu.sms.config.Dict;
import com.clwu.sms.vo.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author: wuchunlong
 * @Date: 2025/11/17 11:30
 * @Description: 异常拦截处理类
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public ResultVo handleBusinessException(BusinessException ex) {
        return ResultVo.error(ex.getCode(), ex.getMessage());
    }

    // 捕获参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResultVo.error(Dict.REST_INT_ARG_ERR, ex.getMessage());
    }

    // 捕获非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo handleIllegalArgument(IllegalArgumentException ex) {
        return ResultVo.error(Dict.REST_INT_ARG_ERR, ex.getMessage());
    }

    // 捕获所有未知异常
    @ExceptionHandler(Exception.class)
    public ResultVo handleException(Exception ex) {
        return ResultVo.error(Dict.REST_INT_ERR, "服务器内部错误，请联系管理员");
    }
}
