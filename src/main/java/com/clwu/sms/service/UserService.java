package com.clwu.sms.service;

import com.clwu.sms.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 11:38
 * @Description:
 **/
public interface UserService {
    /**
     * 根据租户ID反馈下面所有的用户
     * @param tid=NULL返回系统中所有用户，tenantId!=NULL 返回特定租户下用户
     * @return 用户列表
     */
    List<User> getAllUser(Long tid);

    /**
     * 基于传入参数 按照分页形式返回User信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 姓名
     * @param phone 手机号
     * @return 用户列表
     */
    IPage<User> getUserPage(int pageNum, int pageSize, String name, String phone);

    /**
     * 基于用户ID 返回用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 增加用户
     * @param user 用户信息
     */
    void addUser(User user);

    /**
     * 基于用户ID删除用户，注意是软删除
     * @param userId
     */
    void deleUser(Long userId);

    /**
     * 更新用户信息
     * @param user
     */
    void updUser(User user);
}
