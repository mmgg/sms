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
public class ProfitByDrugVo {
    private Long physicId;
    private String physicName;
    private BigDecimal profit;
    private Integer quantity;
    private String typeName;
}
