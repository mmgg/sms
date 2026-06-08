package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@TableName("t_parchase_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParchaseDetail {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long batch;

    @Column(name = "physic")
    private Long physic;

    @Column(name="buying_price")
    private BigDecimal buyingPrice;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "update_user")
    private Long updateUser;

    private int status;

    @Column(name = "num")
    private Integer num;

    @Column(name = "prescription_physic")
    private Long prescriptionPhysic;
}


