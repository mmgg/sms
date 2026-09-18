package com.clwu.sms.controller;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.service.PurchaseDetailService;
import com.clwu.sms.vo.BatchDetailVo;
import com.clwu.sms.vo.PurchasePriceHistoryVo;
import com.clwu.sms.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pd")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService parchaseDetailService;

    @Autowired
    private PhysicService physicService;

    @RequestMapping("add")
    public ResultVo<?> add(@RequestBody PurchaseDetail parchaseDetail) {
        parchaseDetail.setStatus(StatusEnum.US_ENABLED.getCode());
        parchaseDetailService.addPurchaseDetail(parchaseDetail.getBatch(),
                parchaseDetail.getPhysic(), parchaseDetail.getBuyingPrice(), parchaseDetail.getNum());
        return ResultVo.ok(null);
    }

    @PostMapping("upd")
    public ResultVo<?> upd(@RequestBody PurchaseDetail parchaseDetail) {
        parchaseDetailService.updPurchaseDetail(parchaseDetail.getBatch(), parchaseDetail.getPhysic(),
                parchaseDetail.getBuyingPrice(), parchaseDetail.getNum());
        return ResultVo.ok(null);
    }

    @PostMapping("del")
    public ResultVo<?> del(@RequestParam Long pbid, @RequestParam Long pid) {
        parchaseDetailService.delPurchaseDetail(pbid, pid);
        return ResultVo.ok(null);
    }

    @GetMapping("/batchSummary")
    public List<BatchDetailVo> batchSummary(@RequestParam Long batchId) {
        List<PurchaseDetail> details = parchaseDetailService.findByBatch(batchId);
        Map<Long, List<PurchaseDetail>> map = new HashMap<>();
        for (PurchaseDetail d : details) {
            if (!map.containsKey(d.getPhysic())) {
                map.put(d.getPhysic(), new ArrayList<>());
            }
            map.get(d.getPhysic()).add(d);
        }
        List<BatchDetailVo> result = new ArrayList<>();
        for (Long physicId : map.keySet()) {
            Physic physic = physicService.findPhysicById(physicId);
            List<PurchaseDetail> items = map.get(physicId);
            BatchDetailVo vo = new BatchDetailVo();
            vo.setPhysicId(physicId);
            vo.setPhysicName(physic != null ? physic.getName() : "未知");
            vo.setPhysicAlias(physic != null ? physic.getAlias() : "");
            vo.setBuyingPrice(items.get(0).getBuyingPrice());
            vo.setQuantity(items.size());
            int avail = 0, occupy = 0;
            for (PurchaseDetail d : items) {
                if (d.getStatus() == StatusEnum.US_ENABLED.getCode()) avail++;
                else if (d.getStatus() == StatusEnum.US_OCCUPY.getCode()) occupy++;
            }
            vo.setAvailableQty(avail);
            vo.setOccupiedQty(occupy);
            result.add(vo);
        }
        return result;
    }

    @GetMapping("/history")
    public List<PurchasePriceHistoryVo> history(@RequestParam Long physicId) {
        return parchaseDetailService.findPurchaseHistory(physicId);
    }
}
