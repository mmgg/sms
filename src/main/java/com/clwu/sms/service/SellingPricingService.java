package com.clwu.sms.service;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 09:09
 * @Description:
 **/
public interface SellingPricingService {
    /**
     * 增加
     * @param sellingPrice
     */
    public void addSellingPrice(SellingPrice sellingPrice);

    /**
     * 删除
     * @param id
     */
    public void delSellingPrice(Long id);

    /**
     * 更新
     * @param sellingPrice
     */
    public void updSellingPrice(SellingPrice sellingPrice);

    /**
     * 在药品新增或编辑流程中保存当前售价。
     * 同一药品已存在售价时复用原记录，不新增历史售价记录。
     *
     * @param physicId 药品或耗材ID
     * @param price 当前售价
     */
    public void saveOrUpdateForPhysic(Long physicId, BigDecimal price);

    /**
     * 基于sellingPriceID查询
     * @param id
     * @return
     */
    public SellingPrice findSellingPriceById(Long id);

    /**
     * 基于药物ID查询
     * @param pid
     * @param status
     * @return
     */
    public List<SellingPrice> findSellingPriceByPhysic(Long pid, int status);

    /**
     * 返回sellingPrice列表
     * @return
     */
    public List<SellingPrice> listSellingPrice();

}
