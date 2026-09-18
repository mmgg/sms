package com.clwu.sms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clwu.sms.entity.User;
import com.clwu.sms.vo.LoginResultVo;
import java.util.List;

public interface UserService {
    List<User> getAllUser();

    IPage<User> getUserPage(int pageNum, int pageSize, String name, String phone);

    User getUserById(Long userId);

    void addUser(User user);

    void deleUser(Long userId);

    void updUser(User user);

    /** 用户登录，成功返回 User 对象，失败返回 null */
    LoginResultVo login(String tenantCode, String phone, String password);

    /** 设置/修改密码 */
    void setPassword(Long userId, String newPassword);
}
