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
public class ProfitByPrescriptionVo {
    private Long prescriptionId;
    private String patientName;
    private BigDecimal profit;
    private String createTime;
    private Integer itemCount;
}
