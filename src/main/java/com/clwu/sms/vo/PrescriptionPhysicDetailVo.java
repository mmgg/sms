package com.clwu.sms.vo;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import lombok.Data;

@Data
public class PrescriptionPhysicDetailVo {
    private Physic physic;
    private int num;
    private SellingPrice sellingPrice;
}
