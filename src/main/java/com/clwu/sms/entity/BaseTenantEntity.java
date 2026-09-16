package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.clwu.sms.config.LogicalDeleteConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 租户业务实体基础字段。
 *
 * <p>MyBatis-Plus 的租户插件会自动维护 tenantId，逻辑删除插件会自动维护 deleted，
 * 业务代码不再手写这两种公共字段。</p>
 */
@Data
public abstract class BaseTenantEntity {

    /**
     * 所属诊所。该字段由租户插件自动注入和过滤，不直接暴露给前端。
     */
    @TableField("tenant_id")
    @JsonIgnore
    private Long tenantId;

    /**
     * 逻辑删除标记：0 未删除，1 已删除。
     */
    @TableLogic(value = LogicalDeleteConstants.NOT_DELETED, delval = LogicalDeleteConstants.DELETED)
    @TableField("deleted")
    @JsonIgnore
    private Integer deleted;
}
