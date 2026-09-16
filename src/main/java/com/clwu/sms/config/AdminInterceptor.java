package com.clwu.sms.config;

import com.clwu.sms.entity.User;
import com.clwu.sms.enums.UserRoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 租户人员管理权限拦截器。
 */
public class AdminInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AdminInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute(SessionConstants.CURRENT_USER);
        UserRoleEnum role = currentUser == null ? null : currentUser.getRoleEnum();
        if (role != null && role.canManageTenantUsers()) {
            return true;
        }

        log.warn("无权限访问人员管理接口: path={}, userId={}",
                request.getRequestURI(), currentUser == null ? null : currentUser.getId());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"无人员管理权限\",\"data\":null}");
        return false;
    }
}
