package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_perscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long patient;

    private Long user;

    private int status;

    private String comments;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;
}
