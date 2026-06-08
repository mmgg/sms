package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.SellingPricingMapper;
import com.clwu.sms.service.SellingPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 09:14
 * @Description:
 **/
@Service
public class SellingPriceServerImpl implements SellingPricingService {
    @Autowired
    private SellingPricingMapper sellingPricingMapper;
    /**
     * 增加
     *
     * @param sellingPrice
     */
    @Override
    public void addSellingPrice(SellingPrice sellingPrice) {
        sellingPricingMapper.insert(sellingPrice);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delSellingPrice(Long id) {
        UpdateWrapper<SellingPrice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.ne("status", StatusEnum.US_DISABLE.getCode());
        updateWrapper.set("status", StatusEnum.US_DISABLE.getCode());
        if (sellingPricingMapper.update(null, updateWrapper) > 0) {
            return;
        } else {
            // todo 如果原来已经是不可用状态了 打印日志 抛出异常
            return;
        }
    }

    /**
     * 更新
     *
     * @param sellingPrice
     */
    @Override
    public void updSellingPrice(SellingPrice sellingPrice) {
        if (null == sellingPrice.getId() || sellingPrice.getId() <= 0L) {
            return;
        }
        sellingPricingMapper.update(sellingPrice, null);
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
     * @param id
     * @param status
     * @return
     */
    @Override
    public List<SellingPrice> findSellingPriceByPhysic(Long id, int status) {
        QueryWrapper<SellingPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("status", status);
        return sellingPricingMapper.selectList(queryWrapper);
    }
}
