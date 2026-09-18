package com.clwu.sms.controller;

import com.clwu.sms.entity.Tenant;
import com.clwu.sms.entity.User;
import com.clwu.sms.config.SessionConstants;
import com.clwu.sms.enums.UserRoleEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.service.TenantService;
import com.clwu.sms.vo.ResultVo;
import com.clwu.sms.vo.TenantProvisionRequest;
import com.clwu.sms.vo.TenantLicenseUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 租户管理接口：普通员工读取当前诊所，平台管理员可创建诊所和管理员。
 */
@RestController
@RequestMapping("/api/tenants")
@Validated
public class TenantController {

    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    private TenantService tenantService;

    @GetMapping("/current")
    public ResultVo<Tenant> current() {
        return ResultVo.ok(tenantService.getCurrentTenant());
    }

    @GetMapping
    public ResultVo<List<Tenant>> list(HttpSession session) {
        requirePlatformAdmin(session);
        return ResultVo.ok(tenantService.listTenants());
    }

    @PostMapping
    public ResultVo<Tenant> create(@Valid @RequestBody TenantProvisionRequest request,
                                   HttpSession session) {
        requirePlatformAdmin(session);
        Tenant tenant = tenantService.createTenantWithAdmin(request);
        log.info("平台管理员创建诊所: tenantId={}, code={}", tenant.getId(), tenant.getCode());
        return ResultVo.ok(tenant);
    }

    @PostMapping("/license")
    public ResultVo<Tenant> updateLicense(@Valid @RequestBody TenantLicenseUpdateRequest request,
                                          HttpSession session) {
        requirePlatformAdmin(session);
        return ResultVo.ok(tenantService.updateLicense(request));
    }

    private void requirePlatformAdmin(HttpSession session) {
        User current = (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (current == null || current.getRoleEnum() != UserRoleEnum.PLATFORM_ADMIN) {
            throw new BusinessException(403, "仅平台管理员可以管理诊所租户");
        }
    }
}
