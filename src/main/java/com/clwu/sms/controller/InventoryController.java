package com.clwu.sms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.vo.ResultVo;
import com.clwu.sms.vo.StockSummaryVo;
import com.clwu.sms.vo.InventoryDetailGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @GetMapping("/summary")
    public List<StockSummaryVo> summary() {
        return purchaseDetailService.getStockSummary();
    }

    @GetMapping("/detail")
    public List<InventoryDetailGroupVo> detail(@RequestParam Long physicId) {
        QueryWrapper<PurchaseDetail> qw = new QueryWrapper<>();
        qw.eq("physic", physicId);
        qw.orderByDesc("create_time");
        List<PurchaseDetail> details = purchaseDetailMapper.selectList(qw);
        Map<String, List<PurchaseDetail>> grouped = new LinkedHashMap<>();
        for (PurchaseDetail detail : details) {
            String key = String.valueOf(detail.getBatch());
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(detail);
        }
        List<InventoryDetailGroupVo> result = new ArrayList<>();
        for (List<PurchaseDetail> group : grouped.values()) {
            PurchaseDetail first = group.get(0);
            BigDecimal totalPrice = BigDecimal.ZERO;
            int occupiedQty = 0;
            int remainingQty = 0;
            for (PurchaseDetail detail : group) {
                if (detail.getBuyingPrice() != null) {
                    totalPrice = totalPrice.add(detail.getBuyingPrice());
                }
                if (detail.getStatus() == StatusEnum.US_OCCUPY.getCode()) {
                    occupiedQty++;
                } else if (detail.getStatus() == StatusEnum.US_ENABLED.getCode()) {
                    remainingQty++;
                }
            }
            BigDecimal averagePrice = totalPrice.divide(
                    BigDecimal.valueOf(group.size()), 2, RoundingMode.HALF_UP);
            result.add(InventoryDetailGroupVo.builder()
                    .batch(first.getBatch())
                    .physic(first.getPhysic())
                    .buyingPrice(averagePrice)
                    .totalQty(group.size())
                    .occupiedQty(occupiedQty)
                    .remainingQty(remainingQty)
                    .createTime(first.getCreateTime())
                    .build());
        }
        log.info("查询库存明细聚合: physicId={}, groups={}", physicId, result.size());
        return result;
    }

}
