package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 某药品的进货批次和价格。
 */
@Data
@Builder
public class PurchasePriceHistoryVo {

    private Long batch;
    private BigDecimal buyingPrice;
    private Integer availableQty;
    private Integer occupiedQty;
    private Integer totalQty;
    private LocalDateTime createTime;
}
