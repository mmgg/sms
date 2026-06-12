package com.clwu.sms.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSummaryVo {
    private Long physicId;
    private String physicName;
    private String physicAlias;
    private String manufacturer;
    private int type;
    private int unit;
    private int availableQty;
    private int occupiedQty;
    private BigDecimal avgBuyingPrice;
}
