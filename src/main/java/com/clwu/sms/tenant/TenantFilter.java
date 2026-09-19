package com.clwu.sms.tenant;

import com.clwu.sms.config.SessionConstants;
import com.clwu.sms.config.TenantConstants;
import com.clwu.sms.entity.User;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                TenantContext.setTenantId(resolveTenantId(httpRequest));
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            CurrentUserContext.clear();
        }
    }

    /**
     * 已登录请求以 Session 中的租户为准，禁止客户端通过请求头切换到别的诊所。
     * 未登录 API 请求才允许读取 X-Tenant-Id，登录接口则由 tenantCode 自行解析。
     */
    private Long resolveTenantId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConstants.CURRENT_USER) instanceof User) {
            User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);
            CurrentUserContext.setUser(user);
            return user.getTenantId();
        }
        String tenantHeader = request.getHeader(TenantConstants.TENANT_HEADER);
        if (tenantHeader == null || tenantHeader.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(tenantHeader.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
