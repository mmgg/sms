package com.clwu.sms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.vo.ResultVo;
import com.clwu.sms.vo.StockSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @GetMapping("/summary")
    public List<StockSummaryVo> summary() {
        return purchaseDetailService.getStockSummary();
    }

    @GetMapping("/detail")
    public List<PurchaseDetail> detail(@RequestParam Long physicId) {
        QueryWrapper<PurchaseDetail> qw = new QueryWrapper<>();
        qw.eq("physic", physicId);
        qw.orderByDesc("id");
        return purchaseDetailMapper.selectList(qw);
    }
}
