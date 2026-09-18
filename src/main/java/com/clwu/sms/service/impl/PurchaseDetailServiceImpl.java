package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.PurchaseBatchService;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.vo.StockSummaryVo;
import com.clwu.sms.vo.PurchasePriceHistoryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PurchaseDetailServiceImpl.class);

    @Autowired
    private PurchaseDetailMapper parchaseDetailMapper;

    @Autowired
    private PurchaseBatchService parchaseBatchService;

    @Autowired
    private PhysicService physicService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        if (num == null || num <= 0) return;
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) return;

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
        log.info("新增进货库存明细: batchId={}, physicId={}, unitPrice={}, quantity={}",
                pbid, pid, price, num);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updPurchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "进货价格必须大于0");
        }
        if (null != num && num >= 0) {
            QueryWrapper<PurchaseDetail> occupiedQuery = new QueryWrapper<>();
            occupiedQuery.eq("batch", pbid)
                    .eq("physic", pid)
                    .eq("status", StatusEnum.US_OCCUPY.getCode());
            int occupiedCount = parchaseDetailMapper.selectCount(occupiedQuery).intValue();
            if (num < occupiedCount) {
                throw new BusinessException(400,
                        "目标数量不能小于已占用库存数量: " + occupiedCount);
            }

            // 已占用库存可能与处方绑定，只替换“可用”明细，避免破坏历史处方成本。
            QueryWrapper<PurchaseDetail> availableQuery = new QueryWrapper<>();
            availableQuery.eq("batch", pbid)
                    .eq("physic", pid)
                    .eq("status", StatusEnum.US_ENABLED.getCode());
            parchaseDetailMapper.delete(availableQuery);
            addPurchaseDetail(pbid, pid, price, num - occupiedCount);
            log.info("调整进货库存: batchId={}, physicId={}, occupied={}, target={}",
                    pbid, pid, occupiedCount, num);
        } else {
            UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("batch", pbid)
                    .eq("physic", pid)
                    .set("buying_price", price);
            parchaseDetailMapper.update(null, updateWrapper);
        }
    }

    @Transactional
    public int updPurchaseDetailOccupy(Long ppid, Long pid, int status, int num) {
        if (ppid == null || ppid <= 0L || pid == null || pid <= 0L || num <= 0) {
            return 0;
        }
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        if (parchaseDetailMapper.selectCountForLock(pid, status) < num) {
            return 0;
        }
        queryWrapper.eq("physic", pid);
        queryWrapper.eq("status", status);
        queryWrapper.last("order by id limit " + num);
        List<PurchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);
        if (parchaseDetails.size() < num) {
            return 0;
        }

        UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream()
                .map(PurchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_OCCUPY.getCode());
        updateWrapper.set("prescription_physic", ppid);
        int updated = parchaseDetailMapper.update(null, updateWrapper);
        log.info("占用库存: prescriptionPhysicId={}, physicId={}, requested={}, updated={}",
                ppid, pid, num, updated);
        return updated;
    }

    @Override
    public int updPurchaseDetailNum(Long ppid, Long pid, int num, int sourceStatus, int targetStatus) {
        if (null == ppid || ppid <= 0L) return 0;
        if (null == pid || pid <= 0L) return 0;
        if (targetStatus == StatusEnum.US_OCCUPY.getCode()) {
            return updPurchaseDetailOccupy(ppid, pid, StatusEnum.US_ENABLED.getCode(), num);
        } else if (targetStatus == StatusEnum.US_ENABLED.getCode()) {
            return updPurchaseDetailEnable(ppid, pid, num, sourceStatus);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delPurchaseDetail(Long pbid, Long pid) {
        if (null == pbid || pbid <= 0) return;
        if (null == pid || pid <= 0) return;
        QueryWrapper<PurchaseDetail> occupiedQuery = new QueryWrapper<>();
        occupiedQuery.eq("batch", pbid)
                .eq("physic", pid)
                .eq("status", StatusEnum.US_OCCUPY.getCode());
        if (parchaseDetailMapper.selectCount(occupiedQuery) > 0) {
            throw new BusinessException(400, "该药品存在处方占用库存，不能直接删除");
        }

        QueryWrapper<PurchaseDetail> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("batch", pbid).eq("physic", pid);
        parchaseDetailMapper.delete(deleteQuery);
        log.info("逻辑删除进货库存明细: batchId={}, physicId={}", pbid, pid);
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
    @Transactional(rollbackFor = Exception.class)
    public int updPurchaseDetailEnable(Long ppid, Long pid, int num, int sourceStatus) {
        if (ppid == null || ppid <= 0L || pid == null || pid <= 0L || num <= 0) {
            return 0;
        }
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription_physic", ppid)
                .eq("physic", pid)
                .eq("status", sourceStatus);
        if (sourceStatus == StatusEnum.US_OCCUPY.getCode()) {
            queryWrapper.last("order by id desc limit " + num);
        } else {
            queryWrapper.last("order by id limit " + num);
        }
        List<PurchaseDetail> parchaseDetails = parchaseDetailMapper.selectList(queryWrapper);
        if (parchaseDetails.isEmpty()) {
            return 0;
        }

        UpdateWrapper<PurchaseDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", parchaseDetails.stream()
                .map(PurchaseDetail::getId).collect(Collectors.toList()));
        updateWrapper.set("status", StatusEnum.US_ENABLED.getCode());
        updateWrapper.set("prescription_physic", 0L);
        int updated = parchaseDetailMapper.update(null, updateWrapper);
        log.info("释放库存: prescriptionPhysicId={}, physicId={}, requested={}, updated={}",
                ppid, pid, num, updated);
        return updated;
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

    @Override
    public List<PurchasePriceHistoryVo> findPurchaseHistory(Long physicId) {
        if (physicId == null || physicId <= 0L) {
            return new ArrayList<>();
        }
        QueryWrapper<PurchaseDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("physic", physicId).orderByDesc("create_time");
        List<PurchaseDetail> details = parchaseDetailMapper.selectList(wrapper);
        java.util.Map<String, List<PurchaseDetail>> grouped = details.stream()
                .collect(Collectors.groupingBy(d -> d.getBatch() + ":" + d.getBuyingPrice()));
        List<PurchasePriceHistoryVo> result = new ArrayList<>();
        for (List<PurchaseDetail> group : grouped.values()) {
            PurchaseDetail first = group.get(0);
            int available = 0;
            int occupied = 0;
            for (PurchaseDetail detail : group) {
                if (detail.getStatus() == StatusEnum.US_ENABLED.getCode()) {
                    available++;
                } else if (detail.getStatus() == StatusEnum.US_OCCUPY.getCode()) {
                    occupied++;
                }
            }
            result.add(PurchasePriceHistoryVo.builder()
                    .batch(first.getBatch())
                    .buyingPrice(first.getBuyingPrice())
                    .availableQty(available)
                    .occupiedQty(occupied)
                    .totalQty(group.size())
                    .createTime(first.getCreateTime())
                    .build());
        }
        result.sort(Comparator.comparing(PurchasePriceHistoryVo::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        log.info("查询药品进货历史: physicId={}, groups={}", physicId, result.size());
        return result;
    }
}
