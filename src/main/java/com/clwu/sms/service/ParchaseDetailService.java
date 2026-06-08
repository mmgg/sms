package com.clwu.sms.service;

import com.clwu.sms.entity.ParchaseDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 15:27
 * @Description:进货详细信息
 **/
public interface ParchaseDetailService {
    /**
     * 增加某一个批次的某种药物信息
     * @param pbid      批次号
     * @param pid       药品id
     * @param num       数量
     * @param price     进货价格
     */
    public void addParchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num);

    /**
     * 更新某一批次的某种药物的信息(主要用于进货时录入数量或者价格错误)
     * @param pbid      批次号
     * @param pid       药品id
     * @param num       数量
     * @param price     价格
     */
    public void updParchaseDetail(Long pbid, Long pid, BigDecimal price, Integer num);

    /**
     * 使某一个批次某种药物一定数量失效
     * @param pbid      批次号
     * @param pid       药品id
     */
    public void delParchaseDetail(Long pbid, Long pid);

    /**
     * 占用
     * @param ppid      处方药品表id
     * @param pid       药品id
     * @param num       需要数量
     * @return          修改数量
     */
    public int updParchaseDetailOccupy(Long ppid, Long pid, int num, int sourceStatus);

    /**
     * 根据数量调整库存表
     * @param ppid          处方中药品明细id
     * @param pid           药品 id
     * @param num           目标数量
     * @param sourceStatus  原状态
     * @param targetStatus  目标状态
     * @return              修改数量
     */
    public int updParchaseDetailNum(Long ppid, Long pid, int num, int sourceStatus, int targetStatus);

    /**
     * 把占用的某一个药物的占用库存修改为可用状态
     *
     * @param pid           对应药物id
     * @param num           数量
     * @param sourceStatus  状态
     * @return
     */
    public int updParchaseDetailEnable(Long pid, int num, int sourceStatus);

    /**
     * 基于查询条件返回满足条件的信息
     * @param pbid      批次号
     * @param pid       药品id
     * @param status    状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return          List
     */
    public void findParchaseDetail(Long pbid, Long pid, int status, LocalDateTime startTime,
                                                     LocalDateTime endTime);

    /**
     * 基于ppid查找所有库存信息表内容
     * @param ppid
     * @param status
     * @return 库存信息 list
     */
    public List<ParchaseDetail> findParchaseDetailByPPid(Long ppid, int status);

}
