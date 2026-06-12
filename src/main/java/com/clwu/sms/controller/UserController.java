package com.clwu.sms.controller;

import com.clwu.sms.entity.User;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.getAllUser(0L);
    }

    @PostMapping("/add")
    public ResultVo<?> addUser(@RequestBody @NotNull(message = "用户信息不能为空") User user, HttpSession session) {
        User current = (User) session.getAttribute("currentUser");
        user.setStatus(StatusEnum.US_ENABLED.getCode());
        if (current != null) {
            user.setTid(current.getTid() != null ? current.getTid() : 0L);
        } else {
            user.setTid(0L);
        }
        userService.addUser(user);
        return ResultVo.ok(null);
    }

    @PostMapping("/del")
    public ResultVo<?> delUser(@Positive(message = "参数不合法") @RequestParam Long uid) {
        userService.deleUser(uid);
        return ResultVo.ok(null);
    }

    @PostMapping("/upd")
    public ResultVo<?> updUser(@RequestBody @NotNull(message = "用户信息不能为空") User user) {
        userService.updUser(user);
        return ResultVo.ok(null);
    }

    @PostMapping("/reset-pwd")
    public ResultVo<?> resetPwd(@RequestParam @Positive Long uid) {
        userService.setPassword(uid, "123456");
        return ResultVo.ok(null);
    }
}
