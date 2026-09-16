package com.clwu.sms.config;

/**
 * 多租户公共常量。
 */
public final class TenantConstants {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String DEFAULT_TENANT_CODE = "default";
    public static final String DEFAULT_TENANT_NAME = "默认诊所";
    public static final Long DEFAULT_TENANT_ID = 1L;

    private TenantConstants() {
    }
}
