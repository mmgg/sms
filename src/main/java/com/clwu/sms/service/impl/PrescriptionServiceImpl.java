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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 10:39
 * @Description:
 **/
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionServiceImpl.class);

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
     * 添加处方,基于Prescription类插入到数据库中，需要测试ID为负值情况
     *
     * @param perscription
     */
    @Override
    public void addPrescription(Prescription perscription) {
        if (perscription == null) {
            throw new BusinessException(400, "插入处方参数为null");
        }
        perscriptionMapper.insert(perscription);
        log.info("新增处方: prescriptionId={}, patientId={}, userId={}",
                perscription.getId(), perscription.getPatient(), perscription.getUser());
    }

    /**
     * 基于处方ID，删除处方方法，是软删除，修改状态。需要测试ID有存在，ID不存在等情况
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delPrescription(Long id) {
        if (id == null || id <= 0L) {
            return;
        }
        Prescription prescription = perscriptionMapper.selectById(id);
        if (prescription == null) {
            return;
        }

        List<PrescriptionPhysic> items = prescriptionPhysicService.findPrescriptionPhysic(
                id, StatusEnum.US_ENABLED.getCode());
        for (PrescriptionPhysic item : items) {
            // 先释放库存，再逻辑删除处方药品，保证不会留下孤立占用。
            prescriptionPhysicService.deleteWithStockRelease(item.getId());
        }
        perscriptionMapper.deleteById(id);
        log.info("删除处方并释放库存: prescriptionId={}, itemCount={}", id, items.size());
    }

    /**
     * 更新处方，基于处方信息更新处方
     *
     * @param perscription
     */
    @Override
    public void updPrescription(Prescription perscription) {
        if (null != perscription.getId() && perscription.getId() > 0L) {
            perscription.setTenantId(null);
            perscription.setDeleted(null);
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
        queryWrapper.eq("status", status);
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        return perscriptionMapper.selectList(queryWrapper);
    }

    /**
     * 为处方中增加药品信息。首先从在处方表中增加一个记录，然后根据request里面的药品list，逐个查询药品信息以及售价信息，
     * 同时根据处方中每个药品判断库存是否够用。
     * 同时根据处方中每个药品判断库存是否够用。
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPrescriptionWithItems(PrescriptionRequest request) {
        validateCreateRequest(request);
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
            if (result != item.getNum()) {
                Physic physic = physicService.findPhysicById(item.getPhysic());
                String name = physic != null ? physic.getName() : "未知";
                throw new BusinessException(500, "占用库存失败: " + name);
            }
            
            // 更新成本/收入
            prescriptionPhysicService.updConstAndIncomeById(pp.getId());
        }

        log.info("创建处方成功: prescriptionId={}, patientId={}, itemCount={}",
                rxId, request.getPatient(), request.getItems().size());
        return rxId;
    }

    private void validateCreateRequest(PrescriptionRequest request) {
        if (request == null) {
            throw new BusinessException(400, "处方参数不能为空");
        }
        if (request.getPatient() == null || request.getPatient() <= 0L
                || request.getUser() == null || request.getUser() <= 0L) {
            throw new BusinessException(400, "患者和开方人不能为空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(400, "处方至少需要一种药品或耗材");
        }
        for (PrescriptionRequest.Item item : request.getItems()) {
            if (item == null || item.getPhysic() == null || item.getPhysic() <= 0L
                    || item.getNum() == null || item.getNum() <= 0) {
                throw new BusinessException(400, "处方药品和数量不合法");
            }
        }
    }

}
