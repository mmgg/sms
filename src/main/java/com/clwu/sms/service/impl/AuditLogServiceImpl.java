package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.entity.AuditLog;
import com.clwu.sms.mapper.AuditLogMapper;
import com.clwu.sms.service.AuditLogService;
import com.clwu.sms.vo.AuditLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public void save(AuditLog auditLog) {
        try {
            auditLogMapper.insert(auditLog);
        } catch (Exception ex) {
            // 审计失败不能影响正常业务。
            log.error("保存审计日志失败: operation={}, uri={}",
                    auditLog.getOperation(), auditLog.getRequestUri(), ex);
        }
    }

    @Override
    public List<AuditLogVo> query(LocalDateTime startTime, LocalDateTime endTime, Long operatorId) {
        QueryWrapper<AuditLog> wrapper = new QueryWrapper<>();
        if (startTime != null) {
            wrapper.ge("create_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("create_time", endTime);
        }
        if (operatorId != null && operatorId > 0L) {
            wrapper.eq("operator_id", operatorId);
        }
        wrapper.orderByDesc("create_time");
        return auditLogMapper.selectList(wrapper).stream()
                .map(item -> AuditLogVo.builder()
                        .id(item.getId())
                        .operatorId(item.getOperatorId())
                        .operatorName(item.getOperatorName())
                        .operation(item.getOperation())
                        .requestMethod(item.getRequestMethod())
                        .requestUri(item.getRequestUri())
                        .parameters(item.getParameters())
                        .success(item.getSuccess() != null && item.getSuccess() == 1)
                        .errorMessage(item.getErrorMessage())
                        .clientIp(item.getClientIp())
                        .createTime(item.getCreateTime())
                        .build())
                .collect(Collectors.toList());
    }
}
