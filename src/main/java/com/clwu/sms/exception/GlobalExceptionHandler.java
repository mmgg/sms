package com.clwu.sms.exception;

import com.clwu.sms.config.Dict;
import com.clwu.sms.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

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
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(item -> item != null && !item.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败: {}", fieldErrors.stream()
                .map(item -> item.getField() + "=" + item.getDefaultMessage())
                .collect(Collectors.joining(", ")));
        return ResultVo.error(Dict.REST_INT_ARG_ERR,
                message.isEmpty() ? "请求参数不正确，请检查后重试" : message);
    }

    @ExceptionHandler(BindException.class)
    public ResultVo<?> handleBindException(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(item -> item != null && !item.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("；"));
        log.warn("请求参数绑定失败: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR,
                message.isEmpty() ? "请求参数不正确，请检查后重试" : message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVo<?> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(item -> item.getMessage())
                .filter(item -> item != null && !item.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("；"));
        log.warn("参数验证失败: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR,
                message.isEmpty() ? "请求参数不正确，请检查后重试" : message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "请求参数不正确，请检查后重试");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo<?> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("缺少请求参数: {}", ex.getParameterName());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "请求参数不完整，请检查后重试");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultVo<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("请求参数类型错误: name={}, value={}", ex.getName(), ex.getValue());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "请求参数格式不正确，请检查后重试");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo<?> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求数据格式错误: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "请求数据格式不正确，请检查后重试");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVo<?> handleDuplicateKey(DuplicateKeyException ex) {
        log.warn("数据唯一性冲突: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "该数据已存在，请勿重复提交");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResultVo<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("数据完整性校验失败: {}", ex.getMessage());
        return ResultVo.error(Dict.REST_INT_ARG_ERR, "数据不符合保存要求，请检查后重试");
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ResultVo.error(Dict.REST_INT_ERR, "服务器内部错误，请联系管理员");
    }
}
