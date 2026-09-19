package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_audit_log")
public class AuditLog extends BaseTenantEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long operatorId;
    private String operatorName;
    private String operation;
    private String requestMethod;
    private String requestUri;
    private String parameters;
    private Integer success;
    private String errorMessage;
    private String clientIp;
    private LocalDateTime createTime;
}
