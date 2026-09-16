package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_selling_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellingPrice extends BaseTenantEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long physic;

    private BigDecimal price;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;
}

