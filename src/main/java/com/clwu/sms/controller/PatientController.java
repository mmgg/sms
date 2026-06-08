package com.clwu.sms.controller;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 13:52
 * @Description:
 **/
@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    /**
     * 添加患者信息
     * @param patient   患者信息
     */
    @PostMapping("/add")
    public void addPatient(@RequestBody @NotNull(message = "患者信息不能为空") Patient patient) {
        patientService.addPatient(patient);
    }

    /**
     * 删除患者信息
     * @param id        患者id
     */
    @PostMapping("/del")
    public void delPatient(@RequestParam @Min(value = 1, message = "id < 1 不满足条件") Long id) {
        patientService.delPatient(id);
    }

    /**
     * 基于id查询患者信息
     * @param id        患者id
     * @return
     */
    @GetMapping("/getById")
    public Patient getPatientById(@RequestParam @Min(value = 1, message = "id < 1 不满足条件") Long id) {
        return patientService.findPatientById(id);
    }

    /**
     * 更新患者信息
     * @param patient   患者信息
     */
    @PostMapping("/upd")
    public void updPatient(@RequestBody @NotNull(message = "患者信息为空") Patient patient) {
        patientService.updPatient(patient);
    }

    /**
     * 基于条件查询患者信息
     * @param name      患者名字
     * @param phone     电话
     * @param addr      地址
     * @param minAge    最小年龄
     * @param maxAge    最大年龄
     * @return
     */
    @GetMapping("/get")
    public List<Patient> findPatient(@RequestParam @NotNull(message = "名字不能为null") String name,
                                     @RequestParam @NotNull(message = "电话不能为null") String phone,
                                     @RequestParam @NotNull(message = "地址不能为null") String addr,
                                     @RequestParam @Min(value = 0, message = "最小年龄要大于0") int minAge,
                                     @RequestParam @Max(value = 120, message = "最大年龄不能大于120") int maxAge) {
        return patientService.findPatient(name, phone, addr, minAge, maxAge);
    }
}
