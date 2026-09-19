package com.clwu.sms.service;

import com.clwu.sms.entity.AuditLog;
import com.clwu.sms.vo.AuditLogVo;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    void save(AuditLog auditLog);

    List<AuditLogVo> query(LocalDateTime startTime, LocalDateTime endTime, Long operatorId);
}
