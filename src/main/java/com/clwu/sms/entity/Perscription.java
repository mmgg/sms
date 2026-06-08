package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@TableName("t_perscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perscription {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long patient;

    @Column(name = "user")
    private Long user;

    private int status;

    private String comments;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "update_user")
    private Long updateUser;

    /*@Column(name = "cost")
    private Double cost;

    @Column(name = "income")
    private Double income;*/
}


