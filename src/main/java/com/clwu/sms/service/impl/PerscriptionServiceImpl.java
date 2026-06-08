package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.Perscription;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PerscriptionMapper;
import com.clwu.sms.service.PerscriptionService;
import com.clwu.sms.vo.PerscriptionDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 10:39
 * @Description:
 **/
@Service
public class PerscriptionServiceImpl implements PerscriptionService {
    @Autowired
    private PerscriptionMapper perscriptionMapper;
    /**
     * 添加处方
     *
     * @param perscription
     */
    @Override
    public void addPerscription(Perscription perscription) {
        perscriptionMapper.insert(perscription);
    }

    /**
     * 删除处方
     *
     * @param id
     */
    @Override
    public void delPerscription(Long id) {
        UpdateWrapper<Perscription> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", StatusEnum.US_DISABLE.getCode());
        perscriptionMapper.update(null, updateWrapper);
    }

    /**
     * 更新处方
     *
     * @param perscription
     */
    @Override
    public void updPerscription(Perscription perscription) {
        if (null != perscription.getId() && perscription.getId() > 0L) {
            perscriptionMapper.updateById(perscription);
        }
    }

    /**
     * 基于处方id查找处方信息
     *
     * @param id
     * @return
     */
    @Override
    public Perscription findPerscriptionById(Long id) {
        if (null != id && id > 0) {
            return perscriptionMapper.selectById(id);
        }
        return null;
    }

    /**
     * 基于条件查找满足条件的处方
     *
     * @param pid       患者id
     * @param status    处方状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 处方列表
     */
    @Override
    public List<Perscription> findPerscriptionById(Long pid, int status, LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    /**
     * 更新处方价格信息
     * @param id
     */
    public void updCostAndIncome(Long id) {
        UpdateWrapper<Perscription> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        perscriptionMapper.update(null, updateWrapper);
    }

    public Double calcCost(Long id) {
        return 0.0;
    }
}
