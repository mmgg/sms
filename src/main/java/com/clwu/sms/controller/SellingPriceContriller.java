package com.clwu.sms.controller;

import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.service.SellingPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class SellingPriceContriller {
    @Autowired
    private SellingPricingService sellingPricingService;
    @PostMapping("/add")
    public void add(@RequestBody @NotNull(message = "参数不能为空") SellingPrice sellingPrice) {
        sellingPricingService.addSellingPrice(sellingPrice);
    }

    @PostMapping("/del")
    public void del(@RequestParam @NotBlank(message = "参数不能为空")
                        @Min(value = 0L, message = "参数不合法") Long id) {
        sellingPricingService.delSellingPrice(id);
    }

    @PostMapping("/upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") SellingPrice sellingPrice) {
        sellingPricingService.updSellingPrice(sellingPrice);
    }

    @GetMapping("/getById")
    public SellingPrice getById(@RequestParam @NotBlank(message = "参数不能为空")
                                @Min(value = 0L, message = "参数不合法")Long id) {
        return sellingPricingService.findSellingPriceById(id);
    }

    @GetMapping("/getList")
    public List<SellingPrice> getByPid(@RequestParam @NotBlank(message = "参数不能为空")
                                       @Min(value = 0L, message = "参数不合法")Long pid,
                                       @RequestParam @Min(value = -1, message = "参数不合法")
                                       @Max(value = 2, message = "参数不合法") int status) {
        return sellingPricingService.findSellingPriceByPhysic(pid, status);
    }
}
