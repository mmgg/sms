package com.clwu.sms.tenant;

import com.clwu.sms.entity.User;
import com.clwu.sms.exception.BusinessException;

/**
 * 请求级当前用户上下文，供 Service 和自动填充使用。
 */
public final class CurrentUserContext {

    private static final Long SYSTEM_USER_ID = 0L;
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void setUser(User user) {
        CURRENT_USER.set(user);
    }

    public static User getUser() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        User user = CURRENT_USER.get();
        return user == null ? null : user.getId();
    }

    /**
     * 后台任务等无登录用户场景使用系统用户。
     */
    public static Long getUserIdOrSystem() {
        Long userId = getUserId();
        return userId == null ? SYSTEM_USER_ID : userId;
    }

    public static Long requireUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new BusinessException(401, "当前用户未登录");
        }
        return userId;
    }

    public static Long getTenantId() {
        User user = CURRENT_USER.get();
        return user == null ? null : user.getTenantId();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
