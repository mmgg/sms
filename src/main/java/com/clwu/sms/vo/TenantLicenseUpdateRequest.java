package com.clwu.sms.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 平台管理员配置租户授权期。
 */
@Data
public class TenantLicenseUpdateRequest {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "授权开始时间不能为空")
    private LocalDateTime licenseStartTime;

    @NotNull(message = "授权结束时间不能为空")
    private LocalDateTime licenseEndTime;

    @Min(value = 0, message = "提醒天数不能小于0")
    private Integer licenseWarningDays;
}
