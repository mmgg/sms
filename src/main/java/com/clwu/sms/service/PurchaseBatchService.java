package com.clwu.sms.service;

import com.clwu.sms.entity.PurchaseBatch;
import com.clwu.sms.entity.PurchaseDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 14:59
 * @Description: 进货批次服务
 **/
public interface PurchaseBatchService {
    /**
     * 增加进货批次
     * @param parchaseBatch
     */
    public void addPurchaseBatch(PurchaseBatch parchaseBatch);

    /**
     * 删除进货批次
     * @param id
     */
    public void delPurchaseBatch(Long id);

    /**
     * 更新批次
     * @param parchaseBatch
     */
    public void updPurchaseBatch(PurchaseBatch parchaseBatch);

    /**
     * 基于id查询进货批次
     * @param id
     */
    public PurchaseBatch findPurchaseById(Long id);

    /**
     * 基于时间查询批次
     * @param startDateTime
     * @param endDateTime
     */
    public List<PurchaseBatch> findPurchaseByDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
