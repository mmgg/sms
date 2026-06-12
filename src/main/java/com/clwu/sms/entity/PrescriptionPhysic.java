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

@TableName("t_prescription_physic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionPhysic {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long physic;

    private Integer num;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;

    private Long prescription;

    private int status;

    // 价格ID
    private Long selling;

    private String remarks;
    // 总成本
    private BigDecimal cost;
    // 总收入
    private BigDecimal income;
}


