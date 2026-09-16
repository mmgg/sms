package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_parchase_batch")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseBatch extends BaseTenantEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long user;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;

    private Integer status;
}

