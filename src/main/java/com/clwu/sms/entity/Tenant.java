package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录时使用的诊所编码，必须全局唯一。
     */
    private String code;

    private String name;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

