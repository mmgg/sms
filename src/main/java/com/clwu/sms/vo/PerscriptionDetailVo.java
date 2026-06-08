package com.clwu.sms.vo;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.entity.Perscription;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 15:36
 * @Description:
 **/
@Data
public class PerscriptionDetailVo {
    // 处方信息
    private Perscription perscription;
    // 患者信息
    private Patient patient;
    // 药品耗材明细信息
    private List<PerscriptionPhysicDetailVo> perscriptionPhysicDetailVos;
    // 利润
    private BigDecimal profit = new BigDecimal("0.0");
}
