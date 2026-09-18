package com.clwu.sms.config;

import com.clwu.sms.entity.User;
import com.clwu.sms.enums.UserTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 医生权限：只允许患者、处方、库存以及处方所需的只读药品查询。
 */
public class DoctorAccessInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DoctorAccessInterceptor.class);

    private static final List<String> ALLOWED_PREFIXES = Arrays.asList(
            "/dashboard",
            "/api/dashboard",
            "/api/user/current",
            "/api/logout",
            "/api/tenants/current",
            "/patients",
            "/patient",
            "/prescription",
            "/pp/",
            "/inventory",
            "/physic/list",
            "/physic/getById",
            "/physic/getByBarcode",
            "/css/",
            "/js/",
            "/images/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (currentUser == null || currentUser.getType() != UserTypeEnum.UT_DOCTOR.getCode()) {
            return true;
        }

        String path = request.getRequestURI();
        for (String prefix : ALLOWED_PREFIXES) {
            if (path.equals(prefix) || path.startsWith(prefix)) {
                return true;
            }
        }

        log.warn("医生访问受限模块: userId={}, path={}", currentUser.getId(), path);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"当前医生账号无权访问该模块\",\"data\":null}");
        return false;
    }
}
