package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.mapper.PrescriptionPhysicMapper;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.service.PrescriptionPhysicService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.vo.PrescriptionPhysicDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 16:40
 * @Description:
 **/
@Service
public class PrescriptionPhysicServiceImpl implements PrescriptionPhysicService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionPhysicServiceImpl.class);

    @Autowired
    private PrescriptionPhysicMapper prescriptionPyhsicMapper;
    @Autowired
    private SellingPricingService sellingPricingService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private PurchaseDetailService parchaseDetailService;

    /**
     * 增加处方药品（需要同步更新库存表）
     *
     * @param prescriptionPhysic
     */
    @Override
    @Transactional
    public void addPrescriptionPhysic(PrescriptionPhysic prescriptionPhysic) {
        // 只负责处方明细落库，库存占用与金额计算由上层事务统一编排。
        prescriptionPyhsicMapper.insert(prescriptionPhysic);
        log.info("新增处方药品: prescriptionId={}, prescriptionPhysicId={}, physicId={}, num={}",
                prescriptionPhysic.getPrescription(), prescriptionPhysic.getId(),
                prescriptionPhysic.getPhysic(), prescriptionPhysic.getNum());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPrescriptionPhysicWithStock(PrescriptionPhysic prescriptionPhysic) {
        addPrescriptionPhysic(prescriptionPhysic);
        occupyOrThrow(prescriptionPhysic.getId(),
                prescriptionPhysic.getPhysic(), prescriptionPhysic.getNum());
        updConstAndIncomeById(prescriptionPhysic.getId());
    }

    /**
     * 删除处方药品
     *
     * @param id
     */
    @Override
    public void delPrescriptionPhysic(Long id) {
        deleteWithStockRelease(id);
    }

    /**
     * 更新处方药品(更新数量的时候注意要调整进货明细表)
     *
     * @param prescriptionPhysic
     */
    @Override
    public void updPrescriptionPhysic(PrescriptionPhysic prescriptionPhysic) {
        if (null == prescriptionPhysic || prescriptionPhysic.getId() <= 0L) {
            return;
        }
        prescriptionPhysic.setTenantId(null);
        prescriptionPhysic.setDeleted(null);
        prescriptionPyhsicMapper.updateById(prescriptionPhysic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithStockRelease(Long id) {
        PrescriptionPhysic source = findPrescriptionPhysicById(id);
        if (source == null) {
            return;
        }
        if (source.getStatus() == StatusEnum.US_ENABLED.getCode()) {
            int occupied = parchaseDetailService.getSizeByPPIdAndStatus(
                    id, StatusEnum.US_OCCUPY.getCode()).intValue();
            int released = parchaseDetailService.updPurchaseDetailEnable(
                    id, source.getPhysic(), occupied, StatusEnum.US_OCCUPY.getCode());
            if (occupied > 0 && released != occupied) {
                throw new BusinessException(500, "释放处方库存失败");
            }
        }
        prescriptionPyhsicMapper.deleteById(id);
        log.info("删除处方药品并释放库存: prescriptionPhysicId={}, physicId={}",
                id, source.getPhysic());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithStockAdjustment(PrescriptionPhysic target) {
        if (target == null || target.getId() == null || target.getId() <= 0L) {
            return;
        }
        PrescriptionPhysic source = findPrescriptionPhysicById(target.getId());
        if (source == null) {
            throw new BusinessException(404, "处方药品不存在");
        }
        if (target.getStatus() == StatusEnum.US_DISABLE.getCode()) {
            deleteWithStockRelease(target.getId());
            return;
        }

        boolean physicChanged = !source.getPhysic().equals(target.getPhysic());
        boolean numChanged = target.getNum() != null && !source.getNum().equals(target.getNum());
        if (physicChanged) {
            releaseOrThrow(source);
            occupyOrThrow(target.getId(), target.getPhysic(), target.getNum());
        } else if (numChanged) {
            releaseOrThrow(source);
            occupyOrThrow(target.getId(), source.getPhysic(), target.getNum());
        }

        target.setTenantId(null);
        target.setDeleted(null);
        prescriptionPyhsicMapper.updateById(target);
        updConstAndIncomeById(target.getId());
        log.info("调整处方药品库存: prescriptionPhysicId={}, physicId={}, num={}",
                target.getId(), target.getPhysic(), target.getNum());
    }

    /**
     * 基于id查询
     *
     * @param id    处方中某一个药物
     * @return
     */
    @Override
    public PrescriptionPhysic findPrescriptionPhysicById(Long id) {
        return prescriptionPyhsicMapper.selectById(id);
    }

    /**
     * 基于处方号查找
     *
     * @param perscriptionId    处方Id
     * @param status            状态
     * @return                  处方下对应药物
     */
    @Override
    public List<PrescriptionPhysic> findPrescriptionPhysic(Long perscriptionId, int status) {
        QueryWrapper<PrescriptionPhysic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription", perscriptionId);
        queryWrapper.eq("status", status);
        return prescriptionPyhsicMapper.selectList(queryWrapper);
    }

    /**
     * 得到处方中有一个药物的详细信息
     * @param id
     * @return
     */
    public PrescriptionPhysicDetailVo findPrescriptionPhysicDetailById(Long id) {
        PrescriptionPhysicDetailVo perscriptionPhysicDetailVo = new PrescriptionPhysicDetailVo();
        // 查出处方中某一个药物的信息
        PrescriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        if (perscriptionPhysic.getStatus() != StatusEnum.US_ENABLED.getCode()) {
            return null;
        }
        // 药物详细信息
        Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
        // 卖出价格信息
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        perscriptionPhysicDetailVo.setPhysic(physic);
        perscriptionPhysicDetailVo.setNum(perscriptionPhysic.getNum());
        perscriptionPhysicDetailVo.setSellingPrice(sellingPrice);
        return perscriptionPhysicDetailVo;
    }

    /**
     * 更新成本和收入信息
     * @param id
     */
    public void updConstAndIncomeById(Long id) {
        BigDecimal cost = calcCostByid(id);
        BigDecimal income = calcIncomeByid(id);
        UpdateWrapper<PrescriptionPhysic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("cost", cost);
        updateWrapper.set("income", income);
        prescriptionPyhsicMapper.update(null, updateWrapper);
    }
    public BigDecimal calcCostByid(Long id) {
        PrescriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        List<PurchaseDetail> parchaseDetailList = parchaseDetailService.findPurchaseDetailByPPid(id,
                StatusEnum.US_OCCUPY.getCode());
        BigDecimal sum = new BigDecimal("0.0");
        // 计算成本信息
        for(PurchaseDetail parchaseDetail: parchaseDetailList) {
            sum = sum.add(parchaseDetail.getBuyingPrice());
        }
        return sum;
    }

    public BigDecimal calcIncomeByid(Long id) {
        PrescriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        return sellingPrice.getPrice().multiply(new BigDecimal(perscriptionPhysic.getNum()));
    }

    private void releaseOrThrow(PrescriptionPhysic source) {
        int occupied = parchaseDetailService.getSizeByPPIdAndStatus(
                source.getId(), StatusEnum.US_OCCUPY.getCode()).intValue();
        if (occupied <= 0) {
            return;
        }
        int released = parchaseDetailService.updPurchaseDetailEnable(
                source.getId(), source.getPhysic(), occupied, StatusEnum.US_OCCUPY.getCode());
        if (released != occupied) {
            throw new BusinessException(500, "释放原处方药品库存失败");
        }
    }

    private void occupyOrThrow(Long prescriptionPhysicId, Long physicId, Integer num) {
        if (physicId == null || num == null || num <= 0) {
            throw new BusinessException(400, "处方药品和数量不能为空");
        }
        int occupied = parchaseDetailService.updPurchaseDetailNum(
                prescriptionPhysicId, physicId, num,
                StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode());
        if (occupied != num) {
            throw new BusinessException(400, "库存不足，无法调整处方药品");
        }
    }
}
