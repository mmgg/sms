package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.mapper.SellingPricingMapper;
import com.clwu.sms.service.SellingPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
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
        validateSellingPrice(sellingPrice, false);
        List<SellingPrice> existing = findActiveByPhysic(sellingPrice.getPhysic());
        if (!existing.isEmpty()) {
            throw new BusinessException(400, "该药品已设置售价，请直接编辑原售价");
        }
        sellingPrice.setStatus(StatusEnum.US_ENABLED.getCode());
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
        validateSellingPrice(sellingPrice, true);
        SellingPrice existing = sellingPricingMapper.selectById(sellingPrice.getId());
        if (existing == null) {
            throw new BusinessException(404, "售价记录不存在");
        }
        if (sellingPrice.getStatus() == null) {
            sellingPrice.setStatus(existing.getStatus());
        }
        if (isEnabled(sellingPrice.getStatus())) {
            List<SellingPrice> activePrices = findActiveByPhysic(sellingPrice.getPhysic());
            boolean duplicated = activePrices.stream()
                    .anyMatch(item -> !item.getId().equals(sellingPrice.getId()));
            if (duplicated) {
                throw new BusinessException(400, "该药品已设置售价，请直接编辑原售价");
            }
        }

        UpdateWrapper<SellingPrice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sellingPrice.getId());
        sellingPrice.setTenantId(null);
        sellingPrice.setDeleted(null);
        sellingPricingMapper.update(sellingPrice, updateWrapper);
        log.info("更新售价: sellingPriceId={}, physicId={}, price={}, status={}",
                sellingPrice.getId(), sellingPrice.getPhysic(), sellingPrice.getPrice(), sellingPrice.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateForPhysic(Long physicId, BigDecimal price) {
        validateSellingPrice(physicId, price);
        QueryWrapper<SellingPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("physic", physicId);
        List<SellingPrice> records = sellingPricingMapper.selectList(queryWrapper);
        if (records.isEmpty()) {
            SellingPrice sellingPrice = new SellingPrice();
            sellingPrice.setPhysic(physicId);
            sellingPrice.setPrice(price);
            sellingPrice.setStatus(StatusEnum.US_ENABLED.getCode());
            sellingPricingMapper.insert(sellingPrice);
            return;
        }

        SellingPrice current = records.stream()
                .sorted(Comparator
                        .comparing((SellingPrice item) -> isEnabled(item.getStatus()))
                        .thenComparing(SellingPrice::getUpdateTime,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SellingPrice::getId))
                .reduce((first, second) -> second)
                .orElse(records.get(0));

        for (SellingPrice record : records) {
            if (!record.getId().equals(current.getId())
                    && (isEnabled(record.getStatus())
                    || record.getDeleted() == null
                    || record.getDeleted() == 0)) {
                UpdateWrapper<SellingPrice> cleanupWrapper = new UpdateWrapper<>();
                cleanupWrapper.eq("id", record.getId())
                        .set("status", StatusEnum.US_DISABLE.getCode())
                        .set("deleted", 1);
                sellingPricingMapper.update(null, cleanupWrapper);
            }
        }

        UpdateWrapper<SellingPrice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", current.getId())
                .set("price", price)
                .set("status", StatusEnum.US_ENABLED.getCode())
                .set("deleted", 0);
        sellingPricingMapper.update(null, updateWrapper);
        log.info("保存药品当前售价: physicId={}, sellingPriceId={}, price={}",
                physicId, current.getId(), price);
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

    private List<SellingPrice> findActiveByPhysic(Long physicId) {
        QueryWrapper<SellingPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("physic", physicId)
                .eq("status", StatusEnum.US_ENABLED.getCode())
                .orderByDesc("update_time")
                .orderByDesc("id");
        return sellingPricingMapper.selectList(queryWrapper);
    }

    private void validateSellingPrice(SellingPrice sellingPrice, boolean requireId) {
        if (requireId && (sellingPrice == null || sellingPrice.getId() == null || sellingPrice.getId() <= 0L)) {
            throw new BusinessException(400, "售价ID不能为空");
        }
        if (sellingPrice == null || sellingPrice.getPhysic() == null || sellingPrice.getPhysic() <= 0L) {
            throw new BusinessException(400, "请选择药品");
        }
        validateSellingPrice(sellingPrice.getPhysic(), sellingPrice.getPrice());
    }

    private void validateSellingPrice(Long physicId, BigDecimal price) {
        if (physicId == null || physicId <= 0L) {
            throw new BusinessException(400, "请选择药品");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "售价必须大于0");
        }
    }

    private boolean isEnabled(Integer status) {
        return status != null && status == StatusEnum.US_ENABLED.getCode();
    }
}
