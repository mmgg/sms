package com.clwu.sms.vo;

import com.clwu.sms.entity.Physic;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionPhysicDetailVo {
    private Physic physic;
    private int num;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private String remarks;
}
