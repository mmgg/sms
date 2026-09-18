package com.clwu.sms.vo;

import com.clwu.sms.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * 登录结果，包含授权到期提醒。
 */
@Data
@Builder
public class LoginResultVo {

    private User user;
    private String warningMessage;
}
