package com.clwu.sms.config;

import com.clwu.sms.entity.Tenant;
import com.clwu.sms.entity.User;
import com.clwu.sms.enums.UserRoleEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.service.TenantService;
import com.clwu.sms.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

/**
 * 每次业务请求都校验租户授权期，防止长期 Session 绕过到期限制。
 */
@Component
public class TenantLicenseInterceptor implements HandlerInterceptor {

    @Autowired
    private TenantService tenantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (currentUser == null || currentUser.getRoleEnum() == UserRoleEnum.PLATFORM_ADMIN) {
            return true;
        }

        try {
            Tenant tenant = tenantService.getCurrentTenant();
            tenantService.validateLicense(tenant);
            return true;
        } catch (BusinessException ex) {
            if (session != null) {
                session.invalidate();
            }
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\""
                        + ex.getMessage() + "\",\"data\":null}");
            } else {
                response.sendRedirect("/login?licenseMessage="
                        + URLEncoder.encode(ex.getMessage(), "UTF-8"));
            }
            return false;
        }
    }
}
