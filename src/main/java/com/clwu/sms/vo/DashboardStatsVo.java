package com.clwu.sms.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsVo {
    private long patients;
    private long physicCount;
    private long stockItems;
    private long monthPrescriptions;
}
