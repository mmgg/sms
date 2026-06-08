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
@TableName("t_tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private Integer updateTime;
}


