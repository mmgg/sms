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
@TableName("t_selling_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellingPrice {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long physic;

    private BigDecimal price;

    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "update_user")
    private Long updateUser;
}


