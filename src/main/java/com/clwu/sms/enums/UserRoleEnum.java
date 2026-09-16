package com.clwu.sms.enums;

/**
 * 用户权限角色。
 *
 * <p>角色与 {@link UserTypeEnum} 分开：用户类型表示医生、护士等业务身份，
 * 角色表示系统操作权限。</p>
 */
public enum UserRoleEnum {

    PLATFORM_ADMIN(1, "平台管理员"),
    TENANT_ADMIN(10, "诊所管理员"),
    STAFF(20, "普通员工");

    private final int code;
    private final String description;

    UserRoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否允许管理当前诊所的人员。
     */
    public boolean canManageTenantUsers() {
        return this == PLATFORM_ADMIN || this == TENANT_ADMIN;
    }

    public static UserRoleEnum findByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}
