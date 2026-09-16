package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.config.TenantConstants;
import com.clwu.sms.entity.Tenant;
import com.clwu.sms.entity.User;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UserRoleEnum;
import com.clwu.sms.enums.UserTypeEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.mapper.TenantMapper;
import com.clwu.sms.service.TenantService;
import com.clwu.sms.service.UserService;
import com.clwu.sms.tenant.TenantContext;
import com.clwu.sms.utils.StringUtil;
import com.clwu.sms.vo.TenantProvisionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private UserService userService;

    @Override
    public Tenant findByCode(String code) {
        if (StringUtil.isBlank(code)) {
            return null;
        }
        QueryWrapper<Tenant> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code.trim().toLowerCase())
                .eq("status", StatusEnum.US_ENABLED.getCode());
        return tenantMapper.selectOne(wrapper);
    }

    @Override
    public Tenant getCurrentTenant() {
        return tenantMapper.selectById(TenantContext.requireTenantId());
    }

    @Override
    public List<Tenant> listTenants() {
        QueryWrapper<Tenant> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        return tenantMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant createTenantWithAdmin(TenantProvisionRequest request) {
        String code = request.getCode().trim().toLowerCase();
        if (findByCode(code) != null) {
            throw new BusinessException(400, "诊所编码已存在: " + code);
        }

        Tenant tenant = Tenant.builder()
                .code(code)
                .name(request.getName().trim())
                .status(StatusEnum.US_ENABLED.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        tenantMapper.insert(tenant);

        Long previousTenantId = TenantContext.getTenantId();
        try {
            // 用户表也受租户插件保护，创建管理员时必须明确绑定到新诊所。
            TenantContext.setTenantId(tenant.getId());
            User admin = new User();
            admin.setName(request.getAdminName().trim());
            admin.setPhone(request.getAdminPhone().trim());
            admin.setType(UserTypeEnum.UT_OTHER.getCode());
            admin.setRole(UserRoleEnum.TENANT_ADMIN.getCode());
            admin.setStatus(StatusEnum.US_ENABLED.getCode());
            userService.addUser(admin);

            if (StringUtil.isNotBlank(request.getAdminPassword())) {
                userService.setPassword(admin.getId(), request.getAdminPassword());
            }
            log.info("租户创建成功: tenantId={}, code={}, adminId={}",
                    tenant.getId(), code, admin.getId());
            return tenant;
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
