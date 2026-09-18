package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.SellingPricingMapper;
import com.clwu.sms.service.SellingPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 09:14
 * @Description:
 **/
@Service
public class SellingPriceServiceImpl implements SellingPricingService {

    private static final Logger log = LoggerFactory.getLogger(SellingPriceServiceImpl.class);

    @Autowired
    private SellingPricingMapper sellingPricingMapper;
    /**
     * 增加
     *
     * @param sellingPrice
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSellingPrice(SellingPrice sellingPrice) {
        sellingPrice.setStatus(StatusEnum.US_ENABLED.getCode());
        UpdateWrapper<SellingPrice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", StatusEnum.US_DISABLE.getCode());
        updateWrapper.eq("physic", sellingPrice.getPhysic());
        sellingPricingMapper.update(null, updateWrapper);
        sellingPricingMapper.insert(sellingPrice);
        log.info("新增有效售价: physicId={}, price={}", sellingPrice.getPhysic(), sellingPrice.getPrice());
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSellingPrice(Long id) {
        if (id == null || id <= 0L) {
            return;
        }
        sellingPricingMapper.deleteById(id);
        log.info("逻辑删除售价记录: sellingPriceId={}", id);
    }

    /**
     * 更新
     *
     * @param sellingPrice
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updSellingPrice(SellingPrice sellingPrice) {
        if (null == sellingPrice.getId() || sellingPrice.getId() <= 0L) {
            return;
        }
        UpdateWrapper<SellingPrice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sellingPrice.getId());
        sellingPrice.setTenantId(null);
        sellingPrice.setDeleted(null);
        sellingPricingMapper.update(sellingPrice, updateWrapper);
    }

    /**
     * 基于sellingPriceID查询
     *
     * @param id
     * @return
     */
    @Override
    public SellingPrice findSellingPriceById(Long id) {
        return sellingPricingMapper.selectById(id);
    }

    /**
     * 基于药物ID查询
     *
     * @param pid
     * @param status
     * @return
     */
    @Override
    public List<SellingPrice> findSellingPriceByPhysic(Long pid, int status) {
        QueryWrapper<SellingPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", status);
        return sellingPricingMapper.selectList(queryWrapper);
    }

    @Override
    public List<SellingPrice> listSellingPrice() {
        QueryWrapper<SellingPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", StatusEnum.US_ENABLED.getCode());
        return sellingPricingMapper.selectList(queryWrapper);
    }
}
