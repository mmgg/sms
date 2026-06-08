package com.clwu.sms.controller;

import com.clwu.sms.entity.*;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.*;
import com.clwu.sms.vo.PerscriptionDetailVo;
import com.clwu.sms.vo.PerscriptionPhysicDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:20
 * @Description: 处方信息
 **/
@RestController
@RequestMapping("/perscription")
public class PerscriptionController {
    @Autowired
    private PerscriptionService perscriptionService;
    @Autowired
    private PerscriptionPhysicService perscriptionPhysicService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private ParchaseDetailService parchaseDetailService;
    @Autowired
    private SellingPricingService sellingPricingService;
    @PostMapping("/add")
    public Long add(@RequestBody @NotNull(message = "参数不能为空") Perscription perscription) {
        perscriptionService.addPerscription(perscription);
        return perscription.getId();
    }

    @PostMapping("del")
    public void del(@RequestParam @NotNull(message = "参数不能为空")
                    @Min(value = 1L, message = "参数必须大于0") Long id) {
        perscriptionService.delPerscription(id);
    }

    @PostMapping("/upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") Perscription perscription) {
        perscriptionService.updPerscription(perscription);
    }

    @GetMapping("/getById")
    public Perscription getByid(@RequestParam @NotNull(message = "参数不能为空")
                                    @Min(value = 1L, message = "参数必须大于0") Long id) {
        return perscriptionService.findPerscriptionById(id);
    }

    @GetMapping("/getDetailById")
    public PerscriptionDetailVo getPerscriptionDetailById(@RequestParam @NotNull(message = "参数不能为空")
                                                              @Min(value = 1L, message = "参数必须大于0") Long id) {
        PerscriptionDetailVo perscriptionDetailVo = new PerscriptionDetailVo();
        Perscription perscription = perscriptionService.findPerscriptionById(id);
        Patient patient = patientService.findPatientById(perscription.getPatient());
        List<PerscriptionPhysic> perscriptionPhysics =
                perscriptionPhysicService.findPrescriptionPhysic(id, StatusEnum.US_ENABLED.getCode());
        BigDecimal ptCost = new BigDecimal("0.0");
        BigDecimal ptInCome = new BigDecimal("0.0");

        List<PerscriptionPhysicDetailVo> perscriptionPhysicDetailVos = new ArrayList<>(perscriptionPhysics.size());
        for(PerscriptionPhysic perscriptionPhysic: perscriptionPhysics) {
            PerscriptionPhysicDetailVo perscriptionPhysicDetailVo = new PerscriptionPhysicDetailVo();
            Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
            SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
            ptInCome = ptInCome.add(sellingPrice.getPrice().multiply(new BigDecimal(perscriptionPhysic.getNum())));
            perscriptionPhysicDetailVo.setPhysic(physic);
            perscriptionPhysicDetailVo.setNum(perscriptionPhysic.getNum());
            perscriptionPhysicDetailVo.setSellingPrice(sellingPrice);
            List<ParchaseDetail> parchaseDetails = parchaseDetailService.findParchaseDetailByPPid(
                    perscriptionPhysic.getId(), StatusEnum.US_OCCUPY.getCode());
            BigDecimal pdCost = new BigDecimal("0.0");
            for(ParchaseDetail parchaseDetail: parchaseDetails) {
                pdCost = pdCost.add(parchaseDetail.getBuyingPrice());
            }
            ptCost = ptCost.add(pdCost);
            perscriptionPhysicDetailVo.setParchaseDetails(parchaseDetails);
            perscriptionPhysicDetailVo.setProfit(sellingPrice.getPrice().multiply(
                    new BigDecimal(perscriptionPhysic.getNum())).subtract(pdCost));
            perscriptionPhysicDetailVos.add(perscriptionPhysicDetailVo);
        }
        perscriptionDetailVo.setPerscription(perscription);
        perscriptionDetailVo.setPatient(patient);
        perscriptionDetailVo.setPerscriptionPhysicDetailVos(perscriptionPhysicDetailVos);
        perscriptionDetailVo.setProfit(ptInCome.subtract(ptCost));
        return perscriptionDetailVo;

    }
}
