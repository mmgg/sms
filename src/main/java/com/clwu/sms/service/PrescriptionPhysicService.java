package com.clwu.sms.service;

import com.clwu.sms.entity.PrescriptionPhysic;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 10:48
 * @Description: 处方药品管理
 **/
public interface PrescriptionPhysicService {
    /**
     * 增加处方药品（需要同步更新库存表）
     * @param prescriptionPhysic
     */
    public void addPrescriptionPhysic(PrescriptionPhysic prescriptionPhysic);

    /**
     * 删除处方药品
     * @param id
     */
    public void delPrescriptionPhysic(Long id);

    /**
     * 更新处方药品(更新数量的时候注意要调整进货明细表)
     * @param prescriptionPhysic
     */
    public void updPrescriptionPhysic(PrescriptionPhysic prescriptionPhysic);

    /**
     * 基于id查询
     * @param id
     * @return
     */
    public PrescriptionPhysic findPrescriptionPhysicById(Long id);

    /**
     * 基于处方号查找
     * @param perscriptionId    处方ID
     * @param status            状态
     * @return
     */
    public List<PrescriptionPhysic> findPrescriptionPhysic(Long perscriptionId, int status);

    /**
     * 更新成本收入信息
     * @param id
     */
    public void updConstAndIncomeById(Long id);
}
