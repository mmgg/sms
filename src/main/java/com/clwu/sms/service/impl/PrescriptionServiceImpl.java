package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.*;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PrescriptionMapper;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.*;
import com.clwu.sms.vo.PrescriptionDetailVo;
import com.clwu.sms.vo.PrescriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.clwu.sms.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 10:39
 * @Description:
 **/
@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    @Autowired
    private PrescriptionMapper perscriptionMapper;
    @Autowired
    private PurchaseDetailMapper parchaseDetailMapper;

    @Autowired
    private PrescriptionPhysicService prescriptionPhysicService;

    @Autowired
    private SellingPricingService sellingPricingService;

    @Autowired
    private PhysicService physicService;
    @Autowired
    private PurchaseDetailService purchaseDetailService;



    /**
     * 添加处方
     *
     * @param perscription
     */
    @Override
    public void addPrescription(Prescription perscription) {
        perscriptionMapper.insert(perscription);
    }

    /**
     * 删除处方
     *
     * @param id
     */
    @Override
    public void delPrescription(Long id) {
        UpdateWrapper<Prescription> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", StatusEnum.US_DISABLE.getCode());
        perscriptionMapper.update(null, updateWrapper);
    }

    /**
     * 更新处方
     *
     * @param perscription
     */
    @Override
    public void updPrescription(Prescription perscription) {
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
    public Prescription findPrescriptionById(Long id) {
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
    public List<Prescription> findPrescriptionById(Long pid, int status, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Prescription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patient", pid);
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        return perscriptionMapper.selectList(queryWrapper);
    }

    /**
     * 更新处方价格信息
     * @param id
     */
    public void updCostAndIncome(Long id) {
        UpdateWrapper<Prescription> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        perscriptionMapper.update(null, updateWrapper);
    }

    public Double calcCost(Long id) {
        return 0.0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPrescriptionWithItems(PrescriptionRequest request) {
        Prescription prescription = new Prescription();
        prescription.setPatient(request.getPatient());
        prescription.setUser(request.getUser());
        prescription.setComments(request.getComments() != null ? request.getComments() : "");
        prescription.setStatus(StatusEnum.US_ENABLED.getCode());
        perscriptionMapper.insert(prescription);
        Long rxId = prescription.getId();
        
        for (PrescriptionRequest.Item item : request.getItems()) {
            int available = parchaseDetailMapper.selectCountForLock(item.getPhysic(), StatusEnum.US_ENABLED.getCode());
            if (available < item.getNum()) {
                Physic physic = physicService.findPhysicById(item.getPhysic());
                String name = physic != null ? physic.getName() : "未知";
                throw new BusinessException(400, "库存不足: " + name + " (需" + item.getNum() + ", 可用" + available + ")");
            }
            
            // 插入处方药品明细
            PrescriptionPhysic pp = new PrescriptionPhysic();
            pp.setPrescription(rxId);
            pp.setPhysic(item.getPhysic());
            pp.setNum(item.getNum());
            pp.setSelling(item.getSelling() != null ? item.getSelling() : 0L);
            pp.setStatus(StatusEnum.US_ENABLED.getCode());
            List<SellingPrice> sellingPriceList = sellingPricingService.findSellingPriceByPhysic(item.getPhysic(), StatusEnum.US_ENABLED.getCode());
            if (sellingPriceList.size() != 1) {
                throw new BusinessException(400, item.getPhysic() + "药品的售价配置有问题，请检查");
            }
            pp.setSelling(sellingPriceList.get(0).getId());
            prescriptionPhysicService.addPrescriptionPhysic(pp);

            
            // 占用库存（可用→已占用）
            int result = purchaseDetailService.updPurchaseDetailNum(pp.getId(), item.getPhysic(),
                    item.getNum(), StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode());
            if (result == 0) {
                Physic physic = physicService.findPhysicById(item.getPhysic());
                String name = physic != null ? physic.getName() : "未知";
                throw new BusinessException(500, "占用库存失败: " + name);
            }
            
            // 更新成本/收入
            prescriptionPhysicService.updConstAndIncomeById(pp.getId());
        }
        
        return rxId;
    }

}