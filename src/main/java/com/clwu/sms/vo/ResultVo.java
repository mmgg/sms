package com.clwu.sms.vo;

import com.clwu.sms.config.Dict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: wuchunlong
 * @Date: 2025/11/17 11:17
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultVo<T> ok(T data) {
        return new ResultVo<>(Dict.REST_INT_OK, Dict.REST_STR_SUCCESS, data);
    }

    public static ResultVo<?> error(int code, String msg) {
        return new ResultVo<>(code, msg, null);
    }

}
