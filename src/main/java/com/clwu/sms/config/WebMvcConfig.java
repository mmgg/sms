package com.clwu.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TenantLicenseInterceptor tenantLicenseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/api/login",
                        "/api/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                );
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns(
                        "/audit",
                        "/api/audit/**",
                        "/user/list",
                        "/user/add",
                        "/user/del",
                        "/user/upd",
                        "/user/reset-pwd"
                );
        registry.addInterceptor(new DoctorAccessInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/api/login",
                        "/favicon.ico",
                        "/error"
                );
        registry.addInterceptor(tenantLicenseInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/api/login",
                        "/api/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                );
    }
}
