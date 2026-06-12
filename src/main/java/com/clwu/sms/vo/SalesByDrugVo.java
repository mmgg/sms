package com.clwu.sms.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesByDrugVo {
    private Long physicId;
    private String physicName;
    private Integer quantity;
    private String typeName;
}
