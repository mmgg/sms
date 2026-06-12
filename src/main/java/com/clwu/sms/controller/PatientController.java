package com.clwu.sms.controller;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.PatientService;
import com.clwu.sms.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @PostMapping("/add")
    public ResultVo<?> addPatient(@RequestBody Patient patient) {
        patient.setCreateUser(1L);
        patient.setStatus(StatusEnum.US_ENABLED.getCode());
        patientService.addPatient(patient);
        return ResultVo.ok(null);
    }

    @PostMapping("/del")
    public ResultVo<?> delPatient(@RequestParam Long id) {
        patientService.delPatient(id);
        return ResultVo.ok(null);
    }

    @GetMapping("/getById")
    public Patient getPatientById(@RequestParam Long id) {
        return patientService.findPatientById(id);
    }

    @PostMapping("/upd")
    public ResultVo<?> updPatient(@RequestBody Patient patient) {
        patientService.updPatient(patient);
        return ResultVo.ok(null);
    }

    @GetMapping("/list")
    public List<Patient> listPatients(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String phone,
            @RequestParam(required = false, defaultValue = "") String addr,
            @RequestParam(required = false, defaultValue = "0") int minAge,
            @RequestParam(required = false, defaultValue = "0") int maxAge) {
        return patientService.findPatient(name, phone, addr, minAge, maxAge);
    }
}
