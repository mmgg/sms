package com.clwu.sms.controller;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.vo.ResultVo;
import com.clwu.sms.vo.PhysicScanResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/physic")
public class PhysicController {

    @Autowired
    private PhysicService physicService;

    @GetMapping("/list")
    public List<Physic> list(@RequestParam(required = false, defaultValue = "") String name,
                             @RequestParam(required = false, defaultValue = "0") int type) {
        return physicService.findPhysicByName(name);
    }

    @GetMapping("/getById")
    public Physic getById(@RequestParam Long id) {
        return physicService.findPhysicById(id);
    }

    @GetMapping("/getByBarcode")
    public Physic getByBarcode(@RequestParam String barcode) {
        return physicService.findPhysicByBarcode(barcode);
    }

    @GetMapping("/scan")
    public PhysicScanResultVo scan(@RequestParam String barcode) {
        return physicService.scanBarcode(barcode);
    }

    @PostMapping("/add")
    public ResultVo<Physic> add(@RequestBody Physic physic) {
        physic.setStatus(StatusEnum.US_ENABLED.getCode());
        physicService.addPhysic(physic);
        return ResultVo.ok(physic);
    }

    @PostMapping("/upd")
    public ResultVo<Physic> upd(@RequestBody Physic physic) {
        physicService.updPhysic(physic);
        return ResultVo.ok(physic);
    }

    @PostMapping("/del")
    public ResultVo<?> del(@RequestParam Long id) {
        physicService.delePhysic(id);
        return ResultVo.ok(null);
    }
}
