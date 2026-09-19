package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
