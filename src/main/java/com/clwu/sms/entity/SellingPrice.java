package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "请选择药品")
    private Long physic;

    @NotNull(message = "请输入售价")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal price;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
