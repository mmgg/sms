package com.clwu.sms.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 创建诊所及首位管理员所需参数。
 */
@Data
public class TenantProvisionRequest {

    @NotBlank(message = "诊所编码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,32}$", message = "诊所编码只能包含字母、数字、下划线和短横线")
    private String code;

    @NotBlank(message = "诊所名称不能为空")
    private String name;

    @NotBlank(message = "管理员姓名不能为空")
    private String adminName;

    @NotBlank(message = "管理员手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "管理员手机号格式不正确")
    private String adminPhone;

    private String adminPassword;
}
