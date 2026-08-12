package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.PurchaseBatchService;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.vo.StockSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseDetailServiceImpl implements PurchaseDetailService {

    @Autowired
    private PurchaseDetailMapper parchaseDetailMapper;

    @Autowired
    private PurchaseBatchService parchaseBatchService;

    @Autowired
    private PhysicService physicService;

    @Override
    public void addPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        if (num <= 0) return;
        if (price.compareTo(new BigDecimal("0.0")) <= 0) return;

        List<PurchaseDetail> parchaseDetails = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            PurchaseDetail parchaseDetail = new PurchaseDetail();
            parchaseDetail.setBatch(pbid);
            parchaseDetail.setPhysic(pid);
            parchaseDetail.setBuyingPrice(price);
            parchaseDetail.setStatus(StatusEnum.US_ENABLED.getCode());
            parchaseDetails.add(parchaseDetail);
        }
        parchaseDetailMapper.insertBatch(parchaseDetails);
    }

    @Override
    public void updPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        if (null != num && num >= 0) {
            delPurchaseDetail(pbid, pid);
            addPurchaseDetail(pbid, pid, price, num);
        } else {
            UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("batch", pbid).eq("physic", pid).set("price", price);
            parchaseDetailMapper.update(null, updateWrapper);
        }
    }

    @Transactional
    public int updPurchaseDetailOccupy(Long ppid, Long pid, int status, int num) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        if (parchaseDetailMapper.selectCountForLock(pid, status) < num) {
            return 0;
        }
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", status);
        queryWrapper.last("order by id limit " + num);
        List<PurchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);

        UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream()
                .map(PurchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_OCCUPY.getCode());
        updateWrapper.set("prescription_physic", ppid);
        return parchaseDetailMapper.update(null, updateWrapper);
    }

    @Override
    public int updPurchaseDetailNum(Long ppid, Long pid, int num, int sourceStatus, int targetStatus) {
        if (null == ppid || ppid <= 0L) return 0;
        if (null == pid || pid <= 0L) return 0;
        if (targetStatus == StatusEnum.US_OCCUPY.getCode()) {
            return updPurchaseDetailOccupy(ppid, pid, StatusEnum.US_ENABLED.getCode(), num);
        } else if (targetStatus == StatusEnum.US_ENABLED.getCode()) {
            return updPurchaseDetailEnable(pid, sourceStatus, num);
        }
        return 0;
    }

    @Override
    public void delPurchaseDetail(Long pbid, Long pid) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("batch", pbid);
        updateWrapper.eq("physic", pid);
        updateWrapper.set("status", StatusEnum.US_DISABLE.getCode());
        parchaseDetailMapper.update(null, updateWrapper);
    }

    @Override
    public void findPurchaseDetail(Long pbid, Long pid, int status,
                                   LocalDateTime startTime, LocalDateTime endTime) {
    }

    @Override
    public List<PurchaseDetail> findPurchaseDetailByPPid(Long ppid, int status) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        return parchaseDetailMapper.selectList(queryWrapper);
    }

    @Override
    public Long getSizeByPPIdAndStatus(Long ppid, int status) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        return parchaseDetailMapper.selectCount(queryWrapper);
    }

    @Override
    public List<PurchaseDetail> findListLockByPPidAndStatus(Long ppid, int status, Boolean isDesc) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid);
        queryWrapper.eq("status", status);
        if (isDesc) {
            queryWrapper.last("order by id desc for update");
        } else {
            queryWrapper.last("order by id for update");
        }
        return parchaseDetailMapper.selectList(queryWrapper);
    }

    @Override
    public int updPurchaseDetailEnable(Long pid, int num, int sourceStatus) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", sourceStatus);
        if (sourceStatus == StatusEnum.US_OCCUPY.getCode()) {
            queryWrapper.last("order by id desc limit " + num);
        } else {
            queryWrapper.last("order by id limit " + num);
        }
        List<PurchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);

        UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream()
                .map(PurchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_ENABLED.getCode());
        return parchaseDetailMapper.update(null, updateWrapper);
    }

    @Override
    public List<PurchaseDetail> findByBatch(Long batchId) {
        QueryWrapper<PurchaseDetail> qw = new QueryWrapper<>();
        qw.eq("batch", batchId);
        qw.ne("status", StatusEnum.US_DISABLE.getCode());
        qw.orderByDesc("id");
        return parchaseDetailMapper.selectList(qw);
    }

    @Override
    public List<StockSummaryVo> getStockSummary() {
        return parchaseDetailMapper.selectStockSummary();
    }
}
