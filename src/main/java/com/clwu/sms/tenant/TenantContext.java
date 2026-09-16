package com.clwu.sms.tenant;

public class TenantContext {
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static Long requireTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null || tenantId <= 0L) {
            throw new IllegalStateException("当前请求未绑定诊所租户");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

