package com.clwu.sms.service;

import com.clwu.sms.entity.ParchaseBatch;
import com.clwu.sms.entity.ParchaseDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 14:59
 * @Description: 进货批次服务
 **/
public interface ParchaseBatchService {
    /**
     * 增加进货批次
     * @param parchaseBatch
     */
    public void addParchaseBatch(ParchaseBatch parchaseBatch);

    /**
     * 删除进货批次
     * @param id
     */
    public void delParchaseBatch(Long id);

    /**
     * 更新批次
     * @param parchaseBatch
     */
    public void updParchaseBatch(ParchaseBatch parchaseBatch);

    /**
     * 基于id查询进货批次
     * @param id
     */
    public ParchaseBatch findParchaseById(Long id);

    /**
     * 基于时间查询批次
     * @param startDateTime
     * @param endDateTime
     */
    public List<ParchaseBatch> findParchaseByDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
