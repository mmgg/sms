package com.clwu.sms.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BatchDetailVo {
    private Long physicId;
    private String physicName;
    private String physicAlias;
    private BigDecimal buyingPrice;
    private int quantity;
    private int availableQty;
    private int occupiedQty;
}
