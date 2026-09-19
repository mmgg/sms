package com.clwu.sms.controller;

import com.clwu.sms.entity.User;
import com.clwu.sms.service.AuditLogService;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.AuditLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @GetMapping("/audit")
    public String page() {
        return "audit/list";
    }

    @GetMapping("/api/audit/list")
    @ResponseBody
    public List<AuditLogVo> list(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
            @RequestParam(required = false) Long operatorId) {
        if (startTime == null) {
            startTime = LocalDateTime.now().minusHours(72);
        }
        return auditLogService.query(startTime, endTime, operatorId);
    }

    @GetMapping("/api/audit/operators")
    @ResponseBody
    public List<User> operators() {
        return userService.getAllUser();
    }
}
