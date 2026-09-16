package com.clwu.sms.controller;

import com.clwu.sms.entity.Patient;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.Prescription;
import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.entity.Tenant;
import com.clwu.sms.entity.User;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.service.PatientService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.PrescriptionPhysicService;
import com.clwu.sms.service.PrescriptionService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.service.TenantService;
import com.clwu.sms.service.UserService;
import com.clwu.sms.vo.PrescriptionPrintItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用处方笺打印页面。
 */
@Controller
public class PrescriptionPrintController {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionPrintController.class);
    private static final DateTimeFormatter PRINT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionPhysicService prescriptionPhysicService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PhysicService physicService;

    @Autowired
    private SellingPricingService sellingPricingService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @GetMapping("/prescription/print")
    public String print(@RequestParam Long id, Model model) {
        Prescription prescription = prescriptionService.findPrescriptionById(id);
        if (prescription == null) {
            throw new BusinessException(404, "处方不存在");
        }

        Patient patient = patientService.findPatientById(prescription.getPatient());
        User doctor = userService.getUserById(prescription.getUser());
        Tenant tenant = tenantService.getCurrentTenant();
        List<PrescriptionPhysic> prescriptionItems = prescriptionPhysicService.findPrescriptionPhysic(
                id, StatusEnum.US_ENABLED.getCode());

        List<PrescriptionPrintItemVo> items = new ArrayList<>(prescriptionItems.size());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PrescriptionPhysic prescriptionPhysic : prescriptionItems) {
            Physic physic = physicService.findPhysicById(prescriptionPhysic.getPhysic());
            SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(prescriptionPhysic.getSelling());
            BigDecimal unitPrice = sellingPrice == null ? BigDecimal.ZERO : sellingPrice.getPrice();
            BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(prescriptionPhysic.getNum()));
            totalAmount = totalAmount.add(amount);

            UnitEnum unit = UnitEnum.findByCode(physic == null ? null : physic.getUnit());
            items.add(PrescriptionPrintItemVo.builder()
                    .physicName(physic == null ? "未知药品" : physic.getName())
                    .alias(physic == null ? "" : physic.getAlias())
                    .manufacturer(physic == null ? "" : physic.getManufacturer())
                    .unitName(unit == null ? "" : unit.getDesc())
                    .quantity(prescriptionPhysic.getNum())
                    .usage(prescriptionPhysic.getRemarks() == null ? "遵医嘱" : prescriptionPhysic.getRemarks())
                    .unitPrice(unitPrice)
                    .amount(amount)
                    .build());
        }

        model.addAttribute("tenant", tenant);
        model.addAttribute("prescription", prescription);
        model.addAttribute("prescriptionTime",
                prescription.getCreateTime() == null ? "-" : prescription.getCreateTime().format(PRINT_TIME_FORMATTER));
        model.addAttribute("patient", patient);
        model.addAttribute("doctorName", doctor == null ? "未知" : doctor.getName());
        model.addAttribute("patientAge", calculateAge(patient));
        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);
        log.info("打印处方: prescriptionId={}, itemCount={}", id, items.size());
        return "prescription/print";
    }

    private String calculateAge(Patient patient) {
        if (patient == null || patient.getBirthday() == null) {
            return "-";
        }
        return String.valueOf(Period.between(patient.getBirthday(), LocalDate.now()).getYears());
    }
}
