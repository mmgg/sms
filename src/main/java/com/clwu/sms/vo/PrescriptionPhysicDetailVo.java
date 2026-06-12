package com.clwu.sms.vo;

import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrescriptionPhysicDetailVo {
    private Physic physic;
    private int num;
    private List<PurchaseDetail> purchaseDetails;
    private SellingPrice sellingPrice;
    private BigDecimal profit = new BigDecimal("0.0");
}
