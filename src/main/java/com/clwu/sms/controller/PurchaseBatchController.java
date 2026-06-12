package com.clwu.sms.controller;

import com.clwu.sms.entity.PurchaseBatch;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.PurchaseBatchService;
import com.clwu.sms.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/pb")
public class PurchaseBatchController {
    @Autowired
    private PurchaseBatchService parchaseBatchService;

    @GetMapping("/list")
    public List<PurchaseBatch> list() {
        return parchaseBatchService.findPurchaseByDateTime(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now().plusYears(10));
    }

    @PostMapping("/add")
    public ResultVo<?> add(@RequestBody PurchaseBatch batch) {
        batch.setStatus(StatusEnum.US_ENABLED.getCode());
        parchaseBatchService.addPurchaseBatch(batch);
        return ResultVo.ok(null);
    }

    @PostMapping("/del")
    public ResultVo<?> del(@RequestParam Long id) {
        parchaseBatchService.delPurchaseBatch(id);
        return ResultVo.ok(null);
    }

    @GetMapping("/getById")
    public PurchaseBatch getById(@RequestParam Long id) {
        return parchaseBatchService.findPurchaseById(id);
    }
}
