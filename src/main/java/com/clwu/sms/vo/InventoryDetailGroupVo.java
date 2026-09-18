package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存明细聚合结果，按批次、药品、状态和单价汇总数量。
 */
@Data
@Builder
public class InventoryDetailGroupVo {

    private Long batch;
    private Long physic;
    private BigDecimal buyingPrice;
    private Integer totalQty;
    private Integer occupiedQty;
    private Integer remainingQty;
    private LocalDateTime createTime;
}
