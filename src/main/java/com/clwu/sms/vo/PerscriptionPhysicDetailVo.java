package com.clwu.sms.vo;

import com.clwu.sms.entity.ParchaseDetail;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/30 13:49
 * @Description: 处方中每个药品详细信息
 **/
@Data
public class PerscriptionPhysicDetailVo {
    // 药品信息
    private Physic physic;
    // 数量
    private int num;
    // 详细库存信息表
    private List<ParchaseDetail> parchaseDetails;
    // 卖出价格信息
    private SellingPrice sellingPrice;
    // 利润
    private BigDecimal profit = new BigDecimal("0.0");
}
