package com.clwu.sms.exception;

import com.clwu.sms.config.Dict;
import com.clwu.sms.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResultVo<?> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: code={}, msg={}", ex.getCode(), ex.getMessage());
        return ResultVo.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("参数校验异常: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVo<?> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("参数验证失败: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ResultVo.error(Dict.REST_INT_ERR, "服务器内部错误，请联系管理员");
    }
}
