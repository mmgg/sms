package com.clwu.sms.controller;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.Prescription;
import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.User;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.service.PatientService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.PrescriptionPhysicService;
import com.clwu.sms.service.PrescriptionService;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.PatientHistoryItemVo;
import com.clwu.sms.vo.PatientHistoryVo;
import com.clwu.sms.vo.PatientVisitRecordVo;
import com.clwu.sms.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private static final LocalDateTime HISTORY_START = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Autowired
    private PatientService patientService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionPhysicService prescriptionPhysicService;

    @Autowired
    private PhysicService physicService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/history")
    public PatientHistoryVo history(@RequestParam Long id) {
        Patient patient = patientService.findPatientById(id);
        if (patient == null) {
            throw new BusinessException(404, "患者不存在");
        }

        List<Prescription> prescriptions = prescriptionService.findPrescriptionById(
                id,
                StatusEnum.US_ENABLED.getCode(),
                HISTORY_START,
                LocalDateTime.now().plusYears(100));
        prescriptions.sort(Comparator.comparing(
                Prescription::getCreateTime,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        List<PatientVisitRecordVo> records = new ArrayList<>(prescriptions.size());
        for (Prescription prescription : prescriptions) {
            User doctor = userService.getUserById(prescription.getUser());
            List<PrescriptionPhysic> prescriptionItems = prescriptionPhysicService.findPrescriptionPhysic(
                    prescription.getId(), StatusEnum.US_ENABLED.getCode());
            List<PatientHistoryItemVo> items = new ArrayList<>(prescriptionItems.size());
            for (PrescriptionPhysic prescriptionPhysic : prescriptionItems) {
                Physic physic = physicService.findPhysicById(prescriptionPhysic.getPhysic());
                UnitEnum unit = UnitEnum.findByCode(physic == null ? null : physic.getUnit());
                items.add(PatientHistoryItemVo.builder()
                        .physicName(physic == null ? "未知药品" : physic.getName())
                        .quantity(prescriptionPhysic.getNum())
                        .unitName(unit == null ? "" : unit.getDesc())
                        .build());
            }

            records.add(PatientVisitRecordVo.builder()
                    .prescriptionId(prescription.getId())
                    .visitTime(prescription.getCreateTime())
                    .doctorName(doctor == null ? "未知" : doctor.getName())
                    .comments(prescription.getComments())
                    .itemCount(items.size())
                    .items(items)
                    .build());
        }

        log.info("查询患者历史就诊记录: patientId={}, recordCount={}", id, records.size());
        return PatientHistoryVo.builder().patient(patient).records(records).build();
    }
}
