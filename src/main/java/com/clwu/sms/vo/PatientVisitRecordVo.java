package com.clwu.sms.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 以处方为载体的历史就诊记录。
 */
@Data
@Builder
public class PatientVisitRecordVo {

    private Long prescriptionId;
    private LocalDateTime visitTime;
    private String doctorName;
    private String comments;
    private Integer itemCount;
    private List<PatientHistoryItemVo> items;
}
