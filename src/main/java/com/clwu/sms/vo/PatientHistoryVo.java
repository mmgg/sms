package com.clwu.sms.vo;

import com.clwu.sms.entity.Patient;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 患者历史就诊信息。
 */
@Data
@Builder
public class PatientHistoryVo {

    private Patient patient;
    private List<PatientVisitRecordVo> records;
}
