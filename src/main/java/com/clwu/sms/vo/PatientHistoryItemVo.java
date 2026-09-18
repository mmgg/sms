package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 患者历史处方中的药品明细。
 */
@Data
@Builder
public class PatientHistoryItemVo {

    private String physicName;
    private Integer quantity;
    private String unitName;
}
