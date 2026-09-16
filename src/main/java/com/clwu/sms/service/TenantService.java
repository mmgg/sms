package com.clwu.sms.service;

import com.clwu.sms.entity.Tenant;
import com.clwu.sms.vo.TenantProvisionRequest;

import java.util.List;

/**
 * 诊所租户管理服务。
 */
public interface TenantService {

    Tenant findByCode(String code);

    Tenant getCurrentTenant();

    List<Tenant> listTenants();

    /**
     * 创建诊所，并同步创建该诊所的唯一初始管理员。
     */
    Tenant createTenantWithAdmin(TenantProvisionRequest request);
}
