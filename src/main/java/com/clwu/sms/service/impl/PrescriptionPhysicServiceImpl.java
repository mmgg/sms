package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PrescriptionPhysicMapper;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.service.PrescriptionPhysicService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.vo.PrescriptionPhysicDetailVo;
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
        // 在处方明细表中插入一条记录
        prescriptionPyhsicMapper.insert(prescriptionPhysic);
        updConstAndIncomeById(prescriptionPhysic.getId());
    }

    /**
     * 删除处方药品
     *
     * @param id
     */
    @Override
    public void delPrescriptionPhysic(Long id) {
        PrescriptionPhysic PrescriptionPhysic = findPrescriptionPhysicById(id);
        UpdateWrapper<PrescriptionPhysic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", StatusEnum.US_DISABLE.getCode());
        prescriptionPyhsicMapper.update(null, updateWrapper);
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
        prescriptionPyhsicMapper.updateById(prescriptionPhysic);
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
        List<PurchaseDetail> parchaseDetailList = parchaseDetailService.findPurchaseDetailByPPid(id,
                StatusEnum.US_OCCUPY.getCode());
        BigDecimal sum = new BigDecimal("0.0");
        // 计算成本信息
        for(PurchaseDetail parchaseDetail: parchaseDetailList) {
            sum = sum.add(parchaseDetail.getBuyingPrice());
        }
        perscriptionPhysicDetailVo.setPhysic(physic);
        perscriptionPhysicDetailVo.setNum(parchaseDetailList.size());
        perscriptionPhysicDetailVo.setPurchaseDetails(parchaseDetailList);
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
}
