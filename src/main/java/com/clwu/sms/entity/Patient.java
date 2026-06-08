package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@TableName("t_patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private LocalDate birthday;

    private String addr;

    private Byte status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "update_user")
    private Long updateUser;

    @Column(name = "create_user")
    private Integer createUser;
}


