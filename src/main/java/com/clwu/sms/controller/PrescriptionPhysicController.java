package com.clwu.sms.controller;

import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.PrescriptionPhysicService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.vo.PrescriptionPhysicDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:28
 * @Description: 处方药物表
 **/
@RestController
@RequestMapping("/pp")
public class PrescriptionPhysicController {
    @Autowired
    private PrescriptionPhysicService perscriptionPhysicService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private SellingPricingService sellingPricingService;
    @PostMapping("/add")
    public void add(@RequestBody @NotNull(message = "参数不能为空") PrescriptionPhysic perscriptionPhysic) {
        perscriptionPhysic.setStatus(StatusEnum.US_ENABLED.getCode());
        perscriptionPhysicService.addPrescriptionPhysicWithStock(perscriptionPhysic);
    }

    @PostMapping("/del")
    public void del(@RequestParam @NotNull(message = "参数不能为空")
                    @Min(value = 1L, message = "参数必须大于 0") Long id) {
        perscriptionPhysicService.deleteWithStockRelease(id);
    }

    @PostMapping("/upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") PrescriptionPhysic perscriptionPhysic) {
        perscriptionPhysicService.updateWithStockAdjustment(perscriptionPhysic);
    }

    @GetMapping("/getById")
    public PrescriptionPhysic getById(@RequestParam @NotNull(message = "参数不能为空")
                         @Min(value = 1L, message = "参数必须大于 0") Long id) {
        return perscriptionPhysicService.findPrescriptionPhysicById(id);
    }

    @PostMapping("/getList")
    public List<PrescriptionPhysic> get(@RequestParam @NotNull(message = "参数不能为空")
                                        @Min(value = 1L, message = "参数必须大于 0") Long pid,
                                        @RequestParam @Min(value = -1, message = "参数不合法")
                                        @Max(value = 2, message = "参数不合法") int status) {
        return perscriptionPhysicService.findPrescriptionPhysic(pid, status);
    }

    @GetMapping("/getDetailInfoById")
    public PrescriptionPhysicDetailVo getDetailInfoById(@RequestParam @NotNull(message = "参数不能为空")
                                                             @Min(value = 1L, message = "参数必须大于 0") Long id) {
        PrescriptionPhysicDetailVo perscriptionPhysicDetailVo = new PrescriptionPhysicDetailVo();
        PrescriptionPhysic perscriptionPhysic = perscriptionPhysicService.findPrescriptionPhysicById(id);
        if (perscriptionPhysic.getStatus() != StatusEnum.US_ENABLED.getCode()) {
            return null;
        }
        Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        perscriptionPhysicDetailVo.setPhysic(physic);
        perscriptionPhysicDetailVo.setNum(perscriptionPhysic.getNum());
        perscriptionPhysicDetailVo.setSellingPrice(sellingPrice);
        return perscriptionPhysicDetailVo;
    }
}
