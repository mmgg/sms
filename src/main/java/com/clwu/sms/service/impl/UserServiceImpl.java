package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clwu.sms.entity.User;
import com.clwu.sms.entity.UserAuth;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UserTypeEnum;
import com.clwu.sms.mapper.UserAuthMapper;
import com.clwu.sms.mapper.UserMapper;
import com.clwu.sms.service.UserService;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Override
    public List<User> getAllUser(Long tid) {
        if (tid != null && tid != 0L) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tid", tid);
            return userMapper.selectList(queryWrapper);
        }
        return userMapper.selectList(null);
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
    @Transactional
    public void addUser(User user) {
        userMapper.insert(user);
        setPassword(user.getId(), "123456");
    }

    @Override
    public void deleUser(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setStatus(StatusEnum.US_DISABLE.getCode());
            userMapper.updateById(user);
        }
    }

    @Override
    public void updUser(User user) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        if (user.getId() != null && user.getId() > 0L) {
            wrapper.eq("id", user.getId());
            userMapper.update(user, wrapper);
        } else {
            if (StringUtil.isNotEmpty(user.getName())) {
                wrapper.eq("name", user.getName());
            }
            if (StringUtil.isNotEmpty(user.getPhone()) && StringUtil.isPhoneNum(user.getPhone())) {
                wrapper.eq("phone", user.getPhone());
            }
            if (user.getType() != null
                    && user.getType() != UserTypeEnum.UT_UNDEFINE.getCode()
                    && UserTypeEnum.findEnumByCode(user.getType()) != null) {
                wrapper.eq("type", user.getType());
            }
            userMapper.update(user, wrapper);
        }
    }

    @Override
    public User login(String phone, String password) {
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

        return user;
    }

    @Override
    @Transactional
    public void setPassword(Long userId, String newPassword) {
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
            byte[] hash = md.digest(input.getBytes());
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
