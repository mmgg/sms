package com.clwu.sms.vo;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.entity.Prescription;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrescriptionDetailVo {
    private Prescription prescription;
    private Patient patient;
    private List<PrescriptionPhysicDetailVo> prescriptionPhysicDetailVos;
    private BigDecimal profit = new BigDecimal("0.0");
}
