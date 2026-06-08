package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.ParchaseDetail;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.ParchaseDetailMapper;
import com.clwu.sms.service.ParchaseBatchService;
import com.clwu.sms.service.ParchaseDetailService;
import com.clwu.sms.service.PhysicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 15:51
 * @Description:
 **/
@Service
public class ParchaseDetailServiceImpl implements ParchaseDetailService {
    @Autowired
    private ParchaseDetailMapper parchaseDetailMapper;
    @Autowired
    private ParchaseBatchService parchaseBatchService;
    @Autowired
    private PhysicService physicService;
    /**
     * 增加某一个批次的某种药物信息
     *
     * @param pbid  批次号
     * @param pid   药品id
     * @param num   数量
     * @param price 进货价格
     */
    @Override
    public void addParchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) {
            return;
        }
        if (null == pid || pid <= 0) {
            return;
        }
        if (num <= 0) {
            return;
        }
        if (price.compareTo(new BigDecimal("0.0")) <= 0) {
            return;
        }

        List<ParchaseDetail> parchaseDetails = new ArrayList<>(num);
        for (int i=0; i<num; i++) {
            ParchaseDetail parchaseDetail = new ParchaseDetail();
            parchaseDetail.setBatch(pbid);
            parchaseDetail.setPhysic(pid);
            parchaseDetail.setBuyingPrice(price);
            parchaseDetail.setStatus(StatusEnum.US_ENABLED.getCode());
            parchaseDetails.add(parchaseDetail);
        }
        parchaseDetailMapper.insertBatch(parchaseDetails);
    }

    /**
     * 更新某批次的某种药物的信息
     *
     * @param pbid  批次号
     * @param pid   药品id
     * @param price 价格
     * @param num   null，
     */
    @Override
    public void updParchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) {
            return;
        }
        if (null == pid || pid <= 0) {
            return;
        }
        if (null != num  && num >= 0) {
            delParchaseDetail(pbid, pid);
            addParchaseDetail(pbid, pid, price, num);
        } else {
            UpdateWrapper<ParchaseDetail> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("batch", pbid).eq("physic", pbid).set("price", price);
            parchaseDetailMapper.update(null, updateWrapper);
        }
    }

    /**
     * 修改库存表中药品改为占用(只能从可用状态改)
     * @param ppid      处方药品表id
     * @param pid       药品id
     * @param num       需要数量
     * @return          占用数量
     */
    @Transactional
    public int updParchaseDetailOccupy(Long ppid, Long pid, int status, int num) {
        QueryWrapper<ParchaseDetail> queryWrapper = new QueryWrapper<>();
        if (parchaseDetailMapper.selectCountForLock(pid, status) < num) {
            return 0;
        }
        // 根据药品id升序，找到满足条件的num个药品
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", status);
        queryWrapper.last("order by id limit " + num);
        List<ParchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);

        // 将num个药品状态改为占用状态
        UpdateWrapper<ParchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream().
                map(ParchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_OCCUPY.getCode());
        updateWrapper.set("prescription_physic", ppid);
        return parchaseDetailMapper.update(null, updateWrapper);
    }

    /**
     * 把指定数量和状态的药物状态改为可用（可以从占用、初始化、不可用等状态）
     * @param pid
     * @param status
     * @param num
     * @return
     */
    public int updParchaseDetailEnable(Long pid, int status, int num) {
        QueryWrapper<ParchaseDetail> queryWrapper = new QueryWrapper<>();
        if (parchaseDetailMapper.selectCountForLock(pid, status) < num) {
            return 0;
        }
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", status);
        if (status == StatusEnum.US_OCCUPY.getCode()) {
            queryWrapper.last("order by id desc limit " + num);
        } else if(status == StatusEnum.US_INIT.getCode() || status == StatusEnum.US_DISABLE.getCode()) {
            queryWrapper.last("order by id limit " + num);
        }
        List<ParchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);

        UpdateWrapper<ParchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream().
                map(ParchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_ENABLED.getCode());
        return parchaseDetailMapper.update(null, updateWrapper);
    }

    /**
     * 根据数量调整库存表
     *
     * @param ppid          处方中药品明细id
     * @param pid           药品 id
     * @param num           目标数量
     * @param targetStatus  目标状态
     */
    @Override
    public int updParchaseDetailNum(Long ppid, Long pid, int num, int sourceStatus, int targetStatus) {
        if (null == ppid || ppid <= 0L) {
            return 0;
        }
        if (null == pid || pid <= 0L) {
            return 0;
        }
        if (targetStatus == StatusEnum.US_OCCUPY.getCode()) {
            return updParchaseDetailOccupy(ppid, pid, StatusEnum.US_ENABLED.getCode(), num);
        } else if (targetStatus == StatusEnum.US_ENABLED.getCode()) {
            return updParchaseDetailEnable(pid, sourceStatus, num);
        }
        return 0;
    }

    public Long getSizeByPPIdAndStatus(Long ppid, int status) {
        QueryWrapper<ParchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        return parchaseDetailMapper.selectCount(queryWrapper);
    }

    public List<ParchaseDetail> findListLockByPPidAndStatus(Long ppid, int status, Boolean isDesc) {
        QueryWrapper<ParchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        if (isDesc) {
            queryWrapper.last("order by id desc for update");
        } else {
            queryWrapper.last("order by id for update");
        }
        return parchaseDetailMapper.selectList(queryWrapper);
    }

    /**
     * 使某一个批次某种药物一定数量失效
     *
     * @param pbid 批次号
     * @param pid  药品id
     */
    @Override
    public void delParchaseDetail(Long pbid, Long pid) {
        if (null == pbid || pbid <= 0) {
            return;
        }
        if (null == pid || pid <= 0) {
            return;
        }
        UpdateWrapper<ParchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("batch", pbid);
        updateWrapper.eq("physic", pid);
        updateWrapper.set("status", StatusEnum.US_DISABLE.getCode());
        parchaseDetailMapper.update(null, updateWrapper);
    }

    /**
     * 基于查询条件返回满足条件的信息
     *
     * @param pbid      批次号
     * @param pid       药品id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return List
     */
    @Override
    public void findParchaseDetail(Long pbid, Long pid, int status,
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        return ;
    }

    /**
     * 基于ppid查找所有库存信息表内容
     * @param ppid
     * @param status
     * @return 库存信息 list
     */
    public List<ParchaseDetail> findParchaseDetailByPPid(Long ppid, int status) {
        QueryWrapper<ParchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        return parchaseDetailMapper.selectList(queryWrapper);
    }
}
