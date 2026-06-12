package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_physic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Physic {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private int type;

    private int unit;

    private String alias;

    private int status;

    private String manufacturer;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;
}


