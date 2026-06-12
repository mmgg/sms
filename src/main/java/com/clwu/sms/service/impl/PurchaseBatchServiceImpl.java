package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.PurchaseBatch;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PurchaseBatchMapper;
import com.clwu.sms.service.PurchaseBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 15:09
 * @Description: 批次处理
 **/
@Service
public class PurchaseBatchServiceImpl implements PurchaseBatchService {
    @Autowired
    private PurchaseBatchMapper parchaseBatchMapper;
    /**
     * 增加进货批次
     *
     * @param parchaseBatch
     */
    @Override
    public void addPurchaseBatch(PurchaseBatch parchaseBatch) {
        if(null == parchaseBatch) {
            return;
        }
        parchaseBatchMapper.insert(parchaseBatch);
    }

    /**
     * 删除进货批次
     *
     * @param id        进货批次号
     */
    @Override
    public void delPurchaseBatch(Long id) {
        if(null == id || id.compareTo(0L) < 0) {
            return ;
        }
        UpdateWrapper<PurchaseBatch> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", StatusEnum.US_DISABLE.getCode());
        parchaseBatchMapper.update(null, updateWrapper);
    }

    /**
     * 更新批次
     *
     * @param parchaseBatch
     */
    @Override
    public void updPurchaseBatch(PurchaseBatch parchaseBatch) {
        if (null == parchaseBatch || null == parchaseBatch.getId() || parchaseBatch.getId() <= 0L) {
            return;
        }
        parchaseBatchMapper.updateById(parchaseBatch);
    }

    /**
     * 基于id查询进货批次
     *
     * @param id        进货批次号
     */
    @Override
    public PurchaseBatch findPurchaseById(Long id) {
        if (null == id || id <= 0L) {
            return null;
        }
        return parchaseBatchMapper.selectById(id);
    }

    /**
     * 基于时间查询批次
     *
     * @param startDateTime     开始日期
     * @param endDateTime       结束日期
     */
    @Override
    public List<PurchaseBatch> findPurchaseByDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (null == startDateTime || null == endDateTime) {
            return null;
        }
        QueryWrapper<PurchaseBatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("update_time", startDateTime, endDateTime);
        return parchaseBatchMapper.selectList(queryWrapper);
    }
}
