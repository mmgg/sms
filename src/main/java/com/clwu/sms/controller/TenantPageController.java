package com.clwu.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 租户管理页面入口。
 */
@Controller
public class TenantPageController {

    @GetMapping("/tenants")
    public String tenantPage() {
        return "tenant/list";
    }
}
