package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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

    private String spec;

    private String trademark;

    private int status;

    private String manufacturer;

    private String manufacturerAddress;

    private String approvalNumber;

    private String dosage;

    private String indications;

    private String mainIngredients;

    private String contraindications;

    private String precautions;

    private String storageCondition;

    private String validityPeriod;

    private String characteristics;

    private String otherNotes;

    private String note;

    private String imageUrl;

    /**
     * 1=OTC，2=非药品，与本地 type 的含义不同。
     */
    private Integer otcType;

    private String sourceApi;

    private LocalDateTime sourceSyncedAt;

    private String sourcePayload;

    private Integer dataVerified;

    /**
     * 保存药品时一并维护的当前销售价，不持久化到 t_physic。
     */
    @TableField(exist = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigDecimal sellingPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
