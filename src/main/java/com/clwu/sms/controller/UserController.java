package com.clwu.sms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clwu.sms.entity.User;
import com.clwu.sms.service.UserService;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 15:29
 * @Description: 用户接口
 **/
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/getAll")
    public List<User> getAllUsersByTentantId(
            @Positive(message = "租户ID不合法")
            @RequestParam Long tid) {
        return userService.getAllUser(tid);
    }

    @PostMapping("/add")
    public void addUser(@RequestBody @NotNull(message = "用户信息不能为空") User user) {
        // todo 需要根据登陆的user找到tid，把tid塞进去
        user.setUpdateUser(1L);
        userService.addUser(user);

    }
    @PostMapping("/del")
    public void delUser(@Positive(message = "租户ID不合法") @RequestParam Long uid) {
        // todo 判断uid是否是属于对应的tid
        userService.deleUser(uid);
    }

    @PostMapping("/upd")
    public void updUser(@RequestBody @NotNull(message = "用户信息不能为空")User user) {
        // todo 判断这个user是否属于对应的tid
        // todo 更新时间和操作人
        user.setUpdateUser(1L);
        userService.updUser(user);
    }

    @GetMapping("/getUsersPage")
    public IPage<User> getUserPage(@RequestParam @Min(value = 1) int pageNum,
                                   @RequestParam @Min(value = 1) @Max(value = 100) int pageSize,
                                   @RequestParam @NotBlank(message = "参数不能为空") @NotNull(message = "参数不能为空") String phone,
                                   @RequestParam @NotBlank(message = "参数不能为空") @NotNull(message = "参数不能为空") String name
                            ) {
        return userService.getUserPage(pageNum, pageSize, name, phone);
    }
}
