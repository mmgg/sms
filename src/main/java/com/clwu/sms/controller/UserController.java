package com.clwu.sms.controller;

import com.clwu.sms.entity.User;
import com.clwu.sms.config.SessionConstants;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UserRoleEnum;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.getAllUser();
    }

    @PostMapping("/add")
    public ResultVo<?> addUser(@Valid @RequestBody User user) {
        user.setStatus(StatusEnum.US_ENABLED.getCode());
        user.setRole(UserRoleEnum.STAFF.getCode());
        userService.addUser(user);
        return ResultVo.ok(null);
    }

    @PostMapping("/del")
    public ResultVo<?> delUser(@Positive(message = "参数不合法") @RequestParam Long uid,
                               HttpSession session) {
        User current = (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (current != null && uid.equals(current.getId())) {
            return ResultVo.error(400, "不能删除当前登录用户");
        }
        userService.deleUser(uid);
        return ResultVo.ok(null);
    }

    @PostMapping("/upd")
    public ResultVo<?> updUser(@Valid @RequestBody User user) {
        userService.updUser(user);
        return ResultVo.ok(null);
    }

    @PostMapping("/reset-pwd")
    public ResultVo<?> resetPwd(@RequestParam @Positive Long uid) {
        userService.setPassword(uid, "123456");
        log.info("重置用户密码: userId={}", uid);
        return ResultVo.ok(null);
    }
}
