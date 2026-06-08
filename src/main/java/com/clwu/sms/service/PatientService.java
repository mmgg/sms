package com.clwu.sms.service;

import com.clwu.sms.entity.Patient;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 11:46
 * @Description: 病人管理
 **/
public interface PatientService {
    /**
     * 添加患者
     * @param patient
     */
    public void addPatient(Patient patient);

    /**
     * 基于ID查找患者
     * @param pid
     * @return
     */
    public Patient findPatientById(Long pid);

    /**
     * 查找患者
     * @param name      姓名
     * @param phone     电话
     * @param addr      地址
     * @param minAge    最小年龄
     * @param maxAge    最大年龄
     * @return 满足条件列表
     */
    public List<Patient> findPatient(String name, String phone, String addr, Integer minAge, Integer maxAge);

    /**
     * 更新患者信息
     * @param patient
     */
    public void updPatient(Patient patient);

    /**
     * 基于Id删除患者
     * @param pid 患者ID
     */
    public void delPatient(Long pid);
}
