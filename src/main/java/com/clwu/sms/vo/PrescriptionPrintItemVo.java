package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 处方打印明细。
 */
@Data
@Builder
public class PrescriptionPrintItemVo {

    private String physicName;
    private String alias;
    private String manufacturer;
    private String unitName;
    private Integer quantity;
    private String usage;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
