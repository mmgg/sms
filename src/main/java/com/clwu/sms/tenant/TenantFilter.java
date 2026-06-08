package com.clwu.sms.tenant;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {
    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest) {
                String tenantId = ((HttpServletRequest) request).getHeader(TENANT_HEADER);
                TenantContext.setTenantId(tenantId);
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}


