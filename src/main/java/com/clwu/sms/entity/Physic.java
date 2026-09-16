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
public class Physic extends BaseTenantEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private int type;

    private int unit;

    private String alias;

    /**
     * 扫码枪使用的药品/耗材条码。
     */
    private String barcode;

    private int status;

    private String manufacturer;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long updateUser;
}
