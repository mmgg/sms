package com.clwu.sms.audit;

import com.clwu.sms.config.SessionConstants;
import com.clwu.sms.entity.AuditLog;
import com.clwu.sms.entity.User;
import com.clwu.sms.service.AuditLogService;
import com.clwu.sms.tenant.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 统一记录非 GET 业务请求，业务代码不需要手动写审计。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuditLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditLogFilter.class);
    private static final Set<String> AUDIT_METHODS = new HashSet<>(
            Arrays.asList("POST", "PUT", "DELETE", "PATCH"));

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !AUDIT_METHODS.contains(request.getMethod())
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        Throwable error = null;
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            try {
                record(requestWrapper, responseWrapper, error);
            } catch (Exception ex) {
                log.error("审计请求处理失败", ex);
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void record(ContentCachingRequestWrapper request,
                        ContentCachingResponseWrapper response, Throwable error) {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (user == null) {
            return;
        }

        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        boolean success = error == null && response.getStatus() < 400;
        String errorMessage = error == null ? null : error.getMessage();
        if (success && response.getContentType() != null
                && response.getContentType().contains("application/json")) {
            try {
                JsonNode json = objectMapper.readTree(responseBody);
                if (json.has("code")) {
                    success = json.path("code").asInt() == 200;
                    if (!success) {
                        errorMessage = json.path("msg").asText(null);
                    }
                }
            } catch (Exception ignored) {
                // 非标准 JSON 保持 HTTP 状态判断。
            }
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setTenantId(user.getTenantId());
        auditLog.setOperatorId(user.getId());
        auditLog.setOperatorName(user.getName());
        auditLog.setOperation(resolveOperation(request.getRequestURI()));
        auditLog.setRequestMethod(request.getMethod());
        auditLog.setRequestUri(request.getRequestURI());
        auditLog.setParameters(buildParameters(request, responseBody));
        auditLog.setSuccess(success ? 1 : 0);
        auditLog.setErrorMessage(truncate(errorMessage, 1000));
        auditLog.setClientIp(resolveClientIp(request));
        auditLog.setCreateTime(LocalDateTime.now());
        Long previousTenantId = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(user.getTenantId());
            auditLogService.save(auditLog);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private String buildParameters(ContentCachingRequestWrapper request, String responseBody) {
        String query = request.getQueryString();
        String body = request.getContentAsByteArray().length == 0
                ? "" : new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        String parameters = String.join("&",
                query == null ? "" : query,
                body == null ? "" : body);
        return truncate(maskSensitive(parameters), 4000);
    }

    private String maskSensitive(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replaceAll("(?i)(password=)[^&]*", "$1******")
                .replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1******$2")
                .replaceAll("(?i)(\"passwordHash\"\\s*:\\s*\")[^\"]*(\")", "$1******$2");
    }

    private String resolveOperation(String uri) {
        if (uri.contains("/patient/add")) return "新增患者";
        if (uri.contains("/patient/upd")) return "修改患者";
        if (uri.contains("/patient/del")) return "删除患者";
        if (uri.contains("/physic/add")) return "新增药品/耗材";
        if (uri.contains("/physic/upd")) return "修改药品/耗材";
        if (uri.contains("/physic/del")) return "删除药品/耗材";
        if (uri.contains("/prescription/createWithItems")) return "新增处方";
        if (uri.contains("/prescription/updateWithItems")) return "修改处方";
        if (uri.contains("/prescription/del")) return "删除处方";
        if (uri.contains("/pb/add")) return "新增进货批次";
        if (uri.contains("/pb/del")) return "删除进货批次";
        if (uri.contains("/pd/add")) return "新增进货明细";
        if (uri.contains("/pd/upd")) return "修改进货明细";
        if (uri.contains("/pd/del")) return "删除进货明细";
        if (uri.contains("/sp/add")) return "新增售价";
        if (uri.contains("/sp/upd")) return "修改售价";
        if (uri.contains("/sp/del")) return "删除售价";
        if (uri.contains("/user/add")) return "新增用户";
        if (uri.contains("/user/upd")) return "修改用户";
        if (uri.contains("/user/del")) return "删除用户";
        if (uri.contains("/api/login")) return "用户登录";
        if (uri.contains("/api/logout")) return "退出登录";
        return requestMethodFallback(uri);
    }

    private String requestMethodFallback(String uri) {
        return "业务操作 " + uri;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
