package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clwu.sms.config.TenantConstants;
import com.clwu.sms.entity.Tenant;
import com.clwu.sms.entity.User;
import com.clwu.sms.entity.UserAuth;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UserRoleEnum;
import com.clwu.sms.enums.UserTypeEnum;
import com.clwu.sms.mapper.TenantMapper;
import com.clwu.sms.mapper.UserAuthMapper;
import com.clwu.sms.mapper.UserMapper;
import com.clwu.sms.service.UserService;
import com.clwu.sms.tenant.TenantContext;
import com.clwu.sms.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public List<User> getAllUser() {
        // tenant_id 条件由 MyBatis-Plus 租户插件自动追加。
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public IPage<User> getUserPage(int pageNum, int pageSize, String name, String phone) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (StringUtil.isNotEmpty(phone)) {
            wrapper.like("phone", phone);
        }
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        if (user == null) {
            return;
        }
        if (user.getTenantId() == null) {
            user.setTenantId(TenantContext.requireTenantId());
        }
        if (user.getRole() == null) {
            user.setRole(UserRoleEnum.STAFF.getCode());
        }
        if (user.getStatus() == null) {
            user.setStatus(StatusEnum.US_ENABLED.getCode());
        }
        userMapper.insert(user);
        setPassword(user.getId(), DEFAULT_PASSWORD);
        log.info("新增诊所人员: tenantId={}, userId={}, phone={}, role={}",
                user.getTenantId(), user.getId(), user.getPhone(), user.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleUser(Long userId) {
        if (userId == null || userId <= 0L) {
            return;
        }
        User user = getUserById(userId);
        if (user != null) {
            userMapper.deleteById(userId);
            log.info("逻辑删除诊所人员: tenantId={}, userId={}", user.getTenantId(), userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updUser(User user) {
        if (user == null || user.getId() == null || user.getId() <= 0L) {
            return;
        }
        // 租户和权限只能由租户管理流程变更，普通资料编辑不能越权修改。
        user.setTenantId(null);
        user.setRole(null);
        user.setDeleted(null);
        userMapper.updateById(user);
    }

    @Override
    public User login(String tenantCode, String phone, String password) {
        String normalizedCode = StringUtil.isBlank(tenantCode)
                ? TenantConstants.DEFAULT_TENANT_CODE
                : tenantCode.trim().toLowerCase();
        QueryWrapper<Tenant> tenantWrapper = new QueryWrapper<>();
        tenantWrapper.eq("code", normalizedCode)
                .eq("status", StatusEnum.US_ENABLED.getCode());
        Tenant tenant = tenantMapper.selectOne(tenantWrapper);
        if (tenant == null) {
            log.warn("登录失败，诊所编码不存在或已停用: tenantCode={}", normalizedCode);
            return null;
        }

        Long previousTenantId = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(tenant.getId());
            return loginInTenant(phone, password, tenant);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private User loginInTenant(String phone, String password, Tenant tenant) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("status", StatusEnum.US_ENABLED.getCode());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return null;
        }

        QueryWrapper<UserAuth> authWrapper = new QueryWrapper<>();
        authWrapper.eq("user_id", user.getId());
        UserAuth userAuth = userAuthMapper.selectOne(authWrapper);

        if (userAuth == null) {
            return null;
        }

        String inputHash = sha256Hex(password);
        if (!userAuth.getPasswordHash().equals(inputHash)) {
            return null;
        }

        user.setTenantId(tenant.getId());
        log.info("登录成功: tenantId={}, userId={}", tenant.getId(), user.getId());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPassword(Long userId, String newPassword) {
        if (userId == null || userId <= 0L || StringUtil.isBlank(newPassword)) {
            return;
        }
        QueryWrapper<UserAuth> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserAuth existing = userAuthMapper.selectOne(wrapper);

        String hash = sha256Hex(newPassword);
        if (existing != null) {
            existing.setPasswordHash(hash);
            existing.setUpdateTime(LocalDateTime.now());
            userAuthMapper.updateById(existing);
        } else {
            UserAuth auth = UserAuth.builder()
                    .userId(userId)
                    .passwordHash(hash)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            userAuthMapper.insert(auth);
        }
    }

    /** SHA-256 哈希并转为十六进制字符串（兼容 Java 8） */
    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b & 0xFF));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
