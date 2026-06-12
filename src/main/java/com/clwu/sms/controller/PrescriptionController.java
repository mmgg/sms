package com.clwu.sms.controller;

import com.clwu.sms.entity.*;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.*;
import com.clwu.sms.vo.PrescriptionRequest;
import com.clwu.sms.vo.PrescriptionDetailVo;
import com.clwu.sms.vo.ResultVo;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.vo.PrescriptionPhysicDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import com.clwu.sms.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import com.clwu.sms.vo.PrescriptionVO;
/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:20
 * @Description: 处方信息
 **/
@RestController
@RequestMapping("/prescription")
public class PrescriptionController {
    @Autowired
    private PrescriptionService perscriptionService;
    @Autowired
    private PrescriptionPhysicService perscriptionPhysicService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private PurchaseDetailService parchaseDetailService;
    @Autowired
    private SellingPricingService sellingPricingService;
    @Autowired
    private UserService userService;
    @GetMapping("/list")
    public List<PrescriptionVO> list(
            @RequestParam(required = false, defaultValue = "") String patientName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Long patientId = 0L;

        List<Patient> patients = patientService.findPatient(patientName, "", "", 0, 120);
        /*if (!patients.isEmpty()) {
            patientId = patients.get(0).getId();
        } else {
            return Collections.emptyList();
        }
*/
        LocalDateTime start = StringUtil.isNotEmpty(startTime)
                ? LocalDateTime.parse(startTime)
                : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = StringUtil.isNotEmpty(endTime)
                ? LocalDateTime.parse(endTime)
                : LocalDateTime.now().plusYears(100);
        List<PrescriptionVO> result = new ArrayList<>();
        // todo 后续优化，可以通过传入患者id列表直接返回处方列表，减少数据库交互
        for(Patient pt: patients) {
            List<Prescription> ps = perscriptionService.findPrescriptionById(pt.getId(), StatusEnum.US_ENABLED.getCode(),
                    start, end);
            for (Prescription p : ps) {
                Patient patient = patientService.findPatientById(p.getPatient());
                User user = userService.getUserById(p.getUser());
                String pn = patient != null ? patient.getName() : "未知";
                String un = user != null ? user.getName() : "未知";
                result.add(new PrescriptionVO(p.getId(), p.getPatient(), pn, p.getUser(), un,
                        p.getStatus(), p.getComments(), p.getCreateTime(), p.getUpdateTime(), p.getUpdateUser()));
            }
        }
        return result;
    }

    @PostMapping("/createWithItems")
    public ResultVo<?> createWithItems(@RequestBody PrescriptionRequest prescriptionRequest) {
        try {
            Long id = perscriptionService.createPrescriptionWithItems(prescriptionRequest);
            return ResultVo.ok(id);
        } catch (BusinessException e) {
            return ResultVo.error(e.getCode(), e.getMessage());
        }
    }

    @PostMapping("/add")
    public Long add(@RequestBody @NotNull(message = "参数不能为空") Prescription perscription) {
        perscription.setStatus(StatusEnum.US_ENABLED.getCode());
        perscriptionService.addPrescription(perscription);
        return perscription.getId();
    }

    @PostMapping("del")
    public ResultVo<?> del(@RequestParam @NotNull(message = "参数不能为空")
                    @Min(value = 1L, message = "参数必须大于0") Long id) {
        perscriptionService.delPrescription(id);
        return ResultVo.ok(null);
    }

    @PostMapping("/upd")
    public ResultVo<?> upd(@RequestBody @NotNull(message = "参数不能为空") Prescription perscription) {
        perscriptionService.updPrescription(perscription);
        return ResultVo.ok(null);
    }

    @GetMapping("/getById")
    public Prescription getByid(@RequestParam @NotNull(message = "参数不能为空")
                                    @Min(value = 1L, message = "参数必须大于0") Long id) {
        return perscriptionService.findPrescriptionById(id);
    }

    @GetMapping("/getDetailById")
    public PrescriptionDetailVo getPrescriptionDetailById(@RequestParam @NotNull(message = "参数不能为空")
                                                              @Min(value = 1L, message = "参数必须大于0") Long id) {
        PrescriptionDetailVo perscriptionDetailVo = new PrescriptionDetailVo();
        Prescription perscription = perscriptionService.findPrescriptionById(id);
        Patient patient = patientService.findPatientById(perscription.getPatient());
        List<PrescriptionPhysic> perscriptionPhysics =
                perscriptionPhysicService.findPrescriptionPhysic(id, StatusEnum.US_ENABLED.getCode());
        BigDecimal ptCost = new BigDecimal("0.0");
        BigDecimal ptInCome = new BigDecimal("0.0");

        List<PrescriptionPhysicDetailVo> perscriptionPhysicDetailVos = new ArrayList<>(perscriptionPhysics.size());
        for(PrescriptionPhysic perscriptionPhysic: perscriptionPhysics) {
            PrescriptionPhysicDetailVo perscriptionPhysicDetailVo = new PrescriptionPhysicDetailVo();
            Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
            SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
            ptInCome = ptInCome.add(sellingPrice.getPrice().multiply(new BigDecimal(perscriptionPhysic.getNum())));
            perscriptionPhysicDetailVo.setPhysic(physic);
            perscriptionPhysicDetailVo.setNum(perscriptionPhysic.getNum());
            perscriptionPhysicDetailVo.setSellingPrice(sellingPrice);
            List<PurchaseDetail> parchaseDetails = parchaseDetailService.findPurchaseDetailByPPid(
                    perscriptionPhysic.getId(), StatusEnum.US_OCCUPY.getCode());
            BigDecimal pdCost = new BigDecimal("0.0");
            for(PurchaseDetail parchaseDetail: parchaseDetails) {
                pdCost = pdCost.add(parchaseDetail.getBuyingPrice());
            }
            ptCost = ptCost.add(pdCost);
            perscriptionPhysicDetailVo.setPurchaseDetails(parchaseDetails);
            perscriptionPhysicDetailVo.setProfit(sellingPrice.getPrice().multiply(
                    new BigDecimal(perscriptionPhysic.getNum())).subtract(pdCost));
            perscriptionPhysicDetailVos.add(perscriptionPhysicDetailVo);
        }
        perscriptionDetailVo.setPrescription(perscription);
        perscriptionDetailVo.setPatient(patient);
        perscriptionDetailVo.setPrescriptionPhysicDetailVos(perscriptionPhysicDetailVos);
        perscriptionDetailVo.setProfit(ptInCome.subtract(ptCost));
        return perscriptionDetailVo;

    }
}
