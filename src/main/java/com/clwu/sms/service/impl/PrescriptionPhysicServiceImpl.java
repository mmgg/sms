package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.ParchaseDetail;
import com.clwu.sms.entity.PerscriptionPhysic;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PrescriptionPyhsicMapper;
import com.clwu.sms.service.ParchaseDetailService;
import com.clwu.sms.service.PerscriptionPhysicService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.vo.PerscriptionPhysicDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 16:40
 * @Description:
 **/
@Service
public class PrescriptionPhysicServiceImpl implements PerscriptionPhysicService {
    @Autowired
    private PrescriptionPyhsicMapper prescriptionPyhsicMapper;
    @Autowired
    private SellingPricingService sellingPricingService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private ParchaseDetailService parchaseDetailService;
    /**
     * 增加处方药品（需要同步更新库存表）
     *
     * @param prescriptionPhysic
     */
    @Override
    @Transactional
    public void addPerscriptionPhysic(PerscriptionPhysic prescriptionPhysic) {
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
    public void delPerscriptionPhysic(Long id) {
        PerscriptionPhysic PrescriptionPhysic = findPrescriptionPhysicById(id);
        UpdateWrapper<PerscriptionPhysic> updateWrapper = new UpdateWrapper<>();
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
    public void updPerscriptionPhysic(PerscriptionPhysic prescriptionPhysic) {
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
    public PerscriptionPhysic findPrescriptionPhysicById(Long id) {
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
    public List<PerscriptionPhysic> findPrescriptionPhysic(Long perscriptionId, int status) {
        QueryWrapper<PerscriptionPhysic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prescription", perscriptionId);
        queryWrapper.eq("status", status);
        return prescriptionPyhsicMapper.selectList(queryWrapper);
    }

    /**
     * 得到处方中有一个药物的详细信息
     * @param id
     * @return
     */
    public PerscriptionPhysicDetailVo findPerscriptionPhysicDetailById(Long id) {
        PerscriptionPhysicDetailVo perscriptionPhysicDetailVo = new PerscriptionPhysicDetailVo();
        // 查出处方中某一个药物的信息
        PerscriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        if (perscriptionPhysic.getStatus() != StatusEnum.US_ENABLED.getCode()) {
            return null;
        }
        // 药物详细信息
        Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
        // 卖出价格信息
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        List<ParchaseDetail> parchaseDetailList = parchaseDetailService.findParchaseDetailByPPid(id,
                StatusEnum.US_OCCUPY.getCode());
        BigDecimal sum = new BigDecimal("0.0");
        // 计算成本信息
        for(ParchaseDetail parchaseDetail: parchaseDetailList) {
            sum = sum.add(parchaseDetail.getBuyingPrice());
        }
        perscriptionPhysicDetailVo.setPhysic(physic);
        perscriptionPhysicDetailVo.setNum(parchaseDetailList.size());
        perscriptionPhysicDetailVo.setParchaseDetails(parchaseDetailList);
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
        UpdateWrapper<PerscriptionPhysic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("cost", cost);
        updateWrapper.set("income", income);
        prescriptionPyhsicMapper.update(null, updateWrapper);
    }
    public BigDecimal calcCostByid(Long id) {
        PerscriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        List<ParchaseDetail> parchaseDetailList = parchaseDetailService.findParchaseDetailByPPid(id,
                StatusEnum.US_OCCUPY.getCode());
        BigDecimal sum = new BigDecimal("0.0");
        // 计算成本信息
        for(ParchaseDetail parchaseDetail: parchaseDetailList) {
            sum = sum.add(parchaseDetail.getBuyingPrice());
        }
        return sum;
    }

    public BigDecimal calcIncomeByid(Long id) {
        PerscriptionPhysic perscriptionPhysic = findPrescriptionPhysicById(id);
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        return sellingPrice.getPrice().multiply(new BigDecimal(perscriptionPhysic.getNum()));
    }
}
