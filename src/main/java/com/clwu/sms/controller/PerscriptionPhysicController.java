package com.clwu.sms.controller;

import com.clwu.sms.config.Dict;
import com.clwu.sms.entity.ParchaseDetail;
import com.clwu.sms.entity.PerscriptionPhysic;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.ParchaseDetailService;
import com.clwu.sms.service.PerscriptionPhysicService;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.vo.PerscriptionPhysicDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:28
 * @Description: 处方药物表
 **/
@RestController
@RequestMapping("/pp")
public class PerscriptionPhysicController {
    @Autowired
    private PerscriptionPhysicService perscriptionPhysicService;
    @Autowired
    private ParchaseDetailService parchaseDetailService;
    @Autowired
    private PhysicService physicService;
    @Autowired
    private SellingPricingService sellingPricingService;
    @PostMapping("/add")
    public void add(@RequestBody @NotNull(message = "参数不能为空") PerscriptionPhysic perscriptionPhysic) {
        perscriptionPhysicService.addPerscriptionPhysic(perscriptionPhysic);
        parchaseDetailService.updParchaseDetailNum(perscriptionPhysic.getId(),
                perscriptionPhysic.getPhysic(), perscriptionPhysic.getNum(),StatusEnum.US_ENABLED.getCode(),
                StatusEnum.US_OCCUPY.getCode());
        perscriptionPhysicService.updConstAndIncomeById(perscriptionPhysic.getId());
    }

    @PostMapping("/del")
    public void del(@RequestParam @NotNull(message = "参数不能为空")
                    @Min(value = 1L, message = "参数必须大于 0") Long id) {
        perscriptionPhysicService.delPerscriptionPhysic(id);
        PerscriptionPhysic prescriptionPhysic = perscriptionPhysicService.findPrescriptionPhysicById(id);
        parchaseDetailService.updParchaseDetailNum(id, prescriptionPhysic.getPhysic(),
                prescriptionPhysic.getNum(), StatusEnum.US_ENABLED.getCode(), StatusEnum.US_DISABLE.getCode());
        perscriptionPhysicService.updConstAndIncomeById(id);
    }

    @PostMapping("/upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") PerscriptionPhysic perscriptionPhysic) {
        PerscriptionPhysic sourcePP  = perscriptionPhysicService.findPrescriptionPhysicById(perscriptionPhysic.getId());

        if (perscriptionPhysic.getStatus() == StatusEnum.US_DISABLE.getCode() &&
                sourcePP.getStatus() == StatusEnum.US_ENABLED.getCode()) {    // 删除了处方中某一个药物
            // 修改了状态
            parchaseDetailService.updParchaseDetailNum(sourcePP.getId(), sourcePP.getPhysic(),
                    sourcePP.getNum(), StatusEnum.US_OCCUPY.getCode(), StatusEnum.US_ENABLED.getCode());
        } else if (perscriptionPhysic.getStatus() == StatusEnum.US_ENABLED.getCode() &&
                sourcePP.getStatus() == StatusEnum.US_DISABLE.getCode()) {     // 将原删除状态改为生效状态
            parchaseDetailService.updParchaseDetailNum(sourcePP.getId(), sourcePP.getPhysic(),
                    sourcePP.getNum(), StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode());

        } else if (!perscriptionPhysic.getPhysic().equals(sourcePP.getPhysic())) {        // 修改了处方中每个药物种类
            // 把原来的改为可用状态
            parchaseDetailService.updParchaseDetailNum(sourcePP.getId(), sourcePP.getPhysic(),
                    sourcePP.getNum(), StatusEnum.US_OCCUPY.getCode(), StatusEnum.US_ENABLED.getCode());
            // 新添加的改为占用
            parchaseDetailService.updParchaseDetailNum(perscriptionPhysic.getId(), perscriptionPhysic.getPhysic(),
                    perscriptionPhysic.getNum(), StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode());

        } else if (perscriptionPhysic.getNum() != sourcePP.getNum()) {              // 修改了某一个药物的用量
            // 先把原来占用的量释放，再把新的加上
            parchaseDetailService.updParchaseDetailNum(sourcePP.getId(), sourcePP.getPhysic(),
                    sourcePP.getNum(), StatusEnum.US_OCCUPY.getCode(), StatusEnum.US_ENABLED.getCode());
            parchaseDetailService.updParchaseDetailNum(sourcePP.getId(), sourcePP.getPhysic(),
                    perscriptionPhysic.getNum(), StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode());

        }
        perscriptionPhysicService.updPerscriptionPhysic(perscriptionPhysic);
        perscriptionPhysicService.updConstAndIncomeById(perscriptionPhysic.getId());
    }

    @GetMapping("/getById")
    public PerscriptionPhysic getById(@RequestParam @NotNull(message = "参数不能为空")
                         @Min(value = 1L, message = "参数必须大于 0") Long id) {
        return perscriptionPhysicService.findPrescriptionPhysicById(id);
    }

    @PostMapping("/getList")
    public List<PerscriptionPhysic> get(@RequestParam @NotNull(message = "参数不能为空")
                                        @Min(value = 1L, message = "参数必须大于 0") Long pid,
                                        @RequestParam @Min(value = -1, message = "参数不合法")
                                        @Max(value = 2, message = "参数不合法") int status) {
        return perscriptionPhysicService.findPrescriptionPhysic(pid, status);
    }

    @GetMapping("/getDetailInfoById")
    public PerscriptionPhysicDetailVo getDetailInfoById(@RequestParam @NotNull(message = "参数不能为空")
                                                             @Min(value = 1L, message = "参数必须大于 0") Long id) {
        PerscriptionPhysicDetailVo perscriptionPhysicDetailVo = new PerscriptionPhysicDetailVo();
        PerscriptionPhysic perscriptionPhysic = perscriptionPhysicService.findPrescriptionPhysicById(id);
        if (perscriptionPhysic.getStatus() != StatusEnum.US_ENABLED.getCode()) {
            return null;
        }
        Physic physic = physicService.findPhysicById(perscriptionPhysic.getPhysic());
        SellingPrice sellingPrice = sellingPricingService.findSellingPriceById(perscriptionPhysic.getSelling());
        List<ParchaseDetail> parchaseDetailList = parchaseDetailService.findParchaseDetailByPPid(id,
                StatusEnum.US_OCCUPY.getCode());
        BigDecimal sum = Dict.BigDEC_ZERO;
        for(ParchaseDetail parchaseDetail: parchaseDetailList) {
            sum = sum.add(parchaseDetail.getBuyingPrice());
        }
        perscriptionPhysicDetailVo.setPhysic(physic);
        perscriptionPhysicDetailVo.setNum(parchaseDetailList.size());
        perscriptionPhysicDetailVo.setParchaseDetails(parchaseDetailList);
        perscriptionPhysicDetailVo.setSellingPrice(sellingPrice);
        perscriptionPhysicDetailVo.setProfit(perscriptionPhysic.getIncome().subtract(perscriptionPhysic.getCost()));
        return perscriptionPhysicDetailVo;
    }
}
