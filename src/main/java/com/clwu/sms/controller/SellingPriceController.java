package com.clwu.sms.controller;

import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.SellingPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import com.clwu.sms.vo.ResultVo;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:46
 * @Description:
 **/
@RestController
@RequestMapping("/sp")
public class SellingPriceController {
    @Autowired
    private SellingPricingService sellingPricingService;
    @PostMapping("/add")
    public ResultVo<?> add(@RequestBody @NotNull(message = "参数不能为空") SellingPrice sellingPrice) {
        sellingPrice.setStatus(StatusEnum.US_ENABLED.getCode());
        sellingPricingService.addSellingPrice(sellingPrice);
        return ResultVo.ok(null);
    }

    @PostMapping("/del")
    public ResultVo<?> del(@RequestParam @NotBlank(message = "参数不能为空")
                        @Min(value = 0L, message = "参数不合法") Long id) {
        sellingPricingService.delSellingPrice(id);
        return ResultVo.ok(null);
    }

    @PostMapping("/upd")
    public ResultVo<?> upd(@RequestBody @NotNull(message = "参数不能为空") SellingPrice sellingPrice) {
        sellingPricingService.updSellingPrice(sellingPrice);
        return ResultVo.ok(null);
    }

    @GetMapping("/getById")
    public SellingPrice getById(@RequestParam @NotBlank(message = "参数不能为空")
                                @Min(value = 0L, message = "参数不合法")Long id) {
        return sellingPricingService.findSellingPriceById(id);
    }

    @GetMapping("/getByPid")
    public List<SellingPrice> getByPid(@RequestParam @NotBlank(message = "参数不能为空")
                                       @Min(value = 0L, message = "参数不合法")Long pid,
                                       @RequestParam @Min(value = -1, message = "参数不合法")
                                       @Max(value = 2, message = "参数不合法") int status) {
        return sellingPricingService.findSellingPriceByPhysic(pid, status);
    }

    @GetMapping("/list")
    public List<SellingPrice> list() {
        return sellingPricingService.listSellingPrice();
    }
}
