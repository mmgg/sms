package com.clwu.sms.controller;

import com.clwu.sms.entity.ParchaseDetail;
import com.clwu.sms.service.ParchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/29 09:42
 * @Description: 进货详细详细信息表（库存）
 **/
@RestController
@RequestMapping("pd")
public class ParchaseDetailController {
    @Autowired
    private ParchaseDetailService parchaseDetailService;
    @RequestMapping("add")
    public void add(@RequestBody @NotNull(message = "参数不能为空") ParchaseDetail parchaseDetail){
        parchaseDetailService.addParchaseDetail(parchaseDetail.getBatch(),
                parchaseDetail.getPhysic(), parchaseDetail.getBuyingPrice(), parchaseDetail.getNum());
    }

    @PostMapping("upd")
    public void upd(@RequestBody @NotNull(message = "参数不能为空") ParchaseDetail parchaseDetail) {
        parchaseDetailService.updParchaseDetail(parchaseDetail.getBatch(), parchaseDetail.getPhysic(),
                parchaseDetail.getBuyingPrice(), parchaseDetail.getNum());
        return;
    }

    @PostMapping("del")
    public void del(@RequestParam @NotNull(message = "id不能为null")
                                  @Min(value = 1L, message = "id必须大于0") Long pbid,
                                  @RequestParam @NotNull(message = "药品id不能为null") Long pid) {
        parchaseDetailService.delParchaseDetail(pbid, pid);
    }
}
