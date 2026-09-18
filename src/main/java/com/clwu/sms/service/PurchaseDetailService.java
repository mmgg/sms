package com.clwu.sms.service;

import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.vo.StockSummaryVo;
import com.clwu.sms.vo.PurchasePriceHistoryVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseDetailService {
    void addPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num);

    void updPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num);

    int updPurchaseDetailNum(Long ppid, Long pid, int num, int sourceStatus, int targetStatus);

    void delPurchaseDetail(Long pbid, Long pid);

    void findPurchaseDetail(Long pbid, Long pid, int status, LocalDateTime startTime, LocalDateTime endTime);

    List<PurchaseDetail> findPurchaseDetailByPPid(Long ppid, int status);

    Long getSizeByPPIdAndStatus(Long ppid, int status);

    List<PurchaseDetail> findListLockByPPidAndStatus(Long ppid, int status, Boolean isDesc);

    int updPurchaseDetailEnable(Long ppid, Long pid, int num, int sourceStatus);

    /** 获取所有药品的库存汇总 */
    List<StockSummaryVo> getStockSummary();
    List<PurchasePriceHistoryVo> findPurchaseHistory(Long physicId);
    /** 查询某批次的进货明细 */
    List<PurchaseDetail> findByBatch(Long batchId);
}
