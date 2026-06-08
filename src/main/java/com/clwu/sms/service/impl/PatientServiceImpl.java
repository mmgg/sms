package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.Patient;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PatientMapper;
import com.clwu.sms.service.PatientService;
import com.clwu.sms.utils.DateTimeUtil;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 13:09
 * @Description:
 **/
@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    private PatientMapper patientMapper;

    /**
     * 添加患者
     *
     * @param patient
     */
    @Override
    public void addPatient(Patient patient) {
        if (null == patient) {
            return;
        }
        patientMapper.insert(patient);
    }

    /**
     * 基于ID查找患者
     *
     * @param pid
     * @return
     */
    @Override
    public Patient findPatientById(Long pid) {
        if(null == pid || pid.compareTo(0L) < 0) {
            return null;
        }
        return patientMapper.selectById(pid);
    }

    /**
     * 查找患者
     *
     * @param name      姓名
     * @param phone     电话
     * @param addr      地址
     * @param minAge    最小年龄
     * @param maxAge    最大年龄
     * @return 满足条件列表
     */
    @Override
    public List<Patient> findPatient(String name, String phone, String addr, Integer minAge, Integer maxAge) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (StringUtil.isNotEmpty(phone)) {
            queryWrapper.like("phone", phone);
        }
        if (StringUtil.isNotEmpty(addr)) {
            queryWrapper.like("addr", addr);
        }
        // 计算年龄只按照年份字段算，不考虑月份和日期
        if (null != minAge && minAge.compareTo(0) > 0) {
            LocalDate now = LocalDate.now();
            LocalDate minDate = LocalDate.of(now.getYear() - minAge + 1, 1, 1);
            queryWrapper.ge("birthDay", minDate);
        }
        if (null != maxAge && maxAge.compareTo(0) > 0) {
            LocalDate now = LocalDate.now();
            LocalDate maxDate = LocalDate.of(now.getYear() - maxAge, 1, 1);
            queryWrapper.le("birthDay", maxDate);
        }
        return patientMapper.selectList(queryWrapper);
    }

    /**
     * 更新患者信息
     *
     * @param patient
     */
    @Override
    public void updPatient(Patient patient) {
        if (null == patient || patient.getId() == null || patient.getId() <= 0L) {
            return;
        }
        patientMapper.updateById(patient);
    }

    /**
     * 基于Id删除患者
     *
     * @param pid 患者ID
     */
    @Override
    public void delPatient(Long pid) {
        if (pid == null || pid <= 0L) {
            return;
        }
        UpdateWrapper<Patient> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", pid).set("status", StatusEnum.US_DISABLE.getCode());
        patientMapper.update(null, updateWrapper);
    }
}
