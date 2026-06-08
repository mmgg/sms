package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clwu.sms.entity.User;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UserTypeEnum;
import com.clwu.sms.mapper.UserMapper;
import com.clwu.sms.service.UserService;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 11:46
 * @Description:
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    /**
     * 根据租户ID反馈下面所有的用户
     *
     * @param tid =NULL返回系统中所有用户，tenantId!=NULL 返回特定租户下用户
     * @return 用户列表
     */
    @Override
    public List<User> getAllUser(Long tid) {
        if (tid != null || tid != 0L) {
            QueryWrapper<User> queryWrapper = new QueryWrapper();
            queryWrapper.eq("tid", tid);
            return userMapper.selectList(queryWrapper);
        }
        return userMapper.selectList(null);
    }

    /**
     * 基于传入参数 按照分页形式返回User信息
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param name     姓名
     * @param phone    手机号
     * @return 用户列表
     */
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

    /**
     * 基于用户ID 返回用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 增加用户
     *
     * @param user 用户信息
     */
    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    /**
     * 基于用户ID删除用户，注意是软删除
     *
     * @param userId
     */
    @Override
    public void deleUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(StatusEnum.US_DISABLE.getCode());
        userMapper.updateById(user);
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @Override
    public void updUser(User user) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        if (user.getId() > 0L) {
            wrapper.eq("id", user.getId());
            userMapper.update(user,wrapper);
        } else {
            if (StringUtil.isNotEmpty(user.getName())) {
                wrapper.eq("name", user.getName());
            }
            if (StringUtil.isNotEmpty(user.getPhone()) && StringUtil.isPhoneNum(user.getPhone())) {
                wrapper.eq("phone", user.getPhone());
            }
            if (user.getType() != UserTypeEnum.UT_UNDEFINE.getCode() &&
                    UserTypeEnum.findEnumByCode(user.getType()) != null) {
                wrapper.eq("type", user.getType());
            }
        }
        userMapper.update(user, wrapper);
    }
}
