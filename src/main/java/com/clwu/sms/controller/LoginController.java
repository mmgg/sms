package com.clwu.sms.controller;

import com.clwu.sms.entity.User;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 登录API
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResultVo<?> login(@RequestParam String phone,
                             @RequestParam String password,
                             HttpSession session) {
        log.info("用户登录尝试: phone={}", phone);

        User user = userService.login(phone, password);
        if (user == null) {
            log.warn("登录失败: phone={}", phone);
            return ResultVo.error(401, "手机号或密码错误");
        }

        session.setAttribute("currentUser", user);
        log.info("登录成功: user={}, name={}", user.getId(), user.getName());
        return ResultVo.ok(user);
    }

    /**
     * 登出
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResultVo<?> logout(HttpSession session) {
        session.invalidate();
        return ResultVo.ok("已退出登录");
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/api/user/current")
    @ResponseBody
    public ResultVo<?> currentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return ResultVo.error(401, "未登录");
        }
        return ResultVo.ok(user);
    }
}
