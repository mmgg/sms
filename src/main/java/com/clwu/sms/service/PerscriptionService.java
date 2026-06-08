package com.clwu.sms.service;

import com.clwu.sms.entity.Perscription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/26 10:31
 * @Description: 处方
 **/
public interface PerscriptionService {
    /**
     * 添加处方
     * @param perscription
     */
    public void addPerscription(Perscription perscription);

    /**
     * 删除处方
     * @param id
     */
    public void delPerscription(Long id);

    /**
     * 更新处方
     * @param perscription
     */

    public void updPerscription(Perscription perscription);

    /**
     * 基于处方id查找处方信息
     * @param id
     * @return
     */

    public Perscription findPerscriptionById(Long id);

    /**
     * 基于条件查找满足条件的处方
     * @param pid           患者id
     * @param status        处方状态
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return              处方列表
     */
    public List<Perscription> findPerscriptionById(Long pid, int status, LocalDateTime startTime, LocalDateTime endTime);
}
