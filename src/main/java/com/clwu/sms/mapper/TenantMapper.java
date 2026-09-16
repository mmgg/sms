package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 诊所租户持久化接口。
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
