package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogVo {
    private Long id;
    private Long operatorId;
    private String operatorName;
    private String operation;
    private String requestMethod;
    private String requestUri;
    private String parameters;
    private Boolean success;
    private String errorMessage;
    private String clientIp;
    private LocalDateTime createTime;
}
