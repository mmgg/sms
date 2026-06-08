package com.clwu.sms.controller;

import com.clwu.sms.entity.ParchaseBatch;
import com.clwu.sms.service.ParchaseBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 16:12
 * @Description: 进货批次
 **/
@RestController
@RequestMapping("pb")
public class ParchaseBatchController {
    @Autowired
    private ParchaseBatchService parchaseBatchService;
    @PostMapping("/add")
    public void add(@RequestBody @NotNull(message = "参数不能为空") ParchaseBatch parchaseBatch) {
        parchaseBatchService.addParchaseBatch(parchaseBatch);
    }

    @PostMapping("/upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") ParchaseBatch parchaseBatch) {
        parchaseBatchService.updParchaseBatch(parchaseBatch);
    }

    @PostMapping("/del")
    public void del(@RequestParam @NotNull(message = "参数不能为空") @Min(value = 1L, message = "参数要大于0") Long id) {
        parchaseBatchService.delParchaseBatch(id);
    }

    @GetMapping("/getById")
    public ParchaseBatch getById(@RequestParam @NotNull(message = "参数不能为空")
                                  @Min(value = 1L, message = "参数要大于0") Long id) {
        return parchaseBatchService.findParchaseById(id);
    }
}
