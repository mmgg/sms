package com.clwu.sms.controller;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.service.PhysicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 10:58
 * @Description: 药物
 **/
@RestController
@RequestMapping("/physic")
@Validated
public class PhysicController {
    @Autowired
    private PhysicService physicService;

    /**
     * 查找
     * @param pid
     * @return
     */
    @GetMapping("/getById")
    public Physic getPhysicById(@RequestParam @NotNull(message = "药品id不能为null")
                                @Min(value = 0L, message = "药品id必须大于0") Long pid) {

        return physicService.findPhysicById(pid);
    }

    /**
     * 添加
     * @param physic
     */
    @PostMapping("/add")
    public void addPhysic(@RequestBody @NotNull(message = "参数不能为空") Physic physic) {
        physicService.addPhysic(physic);
    }

    /**
     * 失效
     * @param pid
     */
    @PostMapping("/del")
    public void delPhysic(@RequestParam @NotBlank(message = "参数不能为空")
                              @Min(value = 0L, message = "参数不合法") Long pid) {
        physicService.delePhysic(pid);
    }

    /**
     * 更新
     * @param physic
     */
    @PostMapping("/upd")
    public void updPhysic(@RequestBody @NotNull(message = "参数不合法") Physic physic) {
        physicService.updPhysic(physic);
    }

    /**
     * 预计名字查找
     * @param name 名字
     * @param status
     * @return
     */
    @GetMapping("/getByName")
    public List<Physic> getPhysicByName(@RequestParam String name, @RequestParam int status) {
        return physicService.findPhysicByName(name);
    }

    /**
     * 基于别名查找
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/getByAlias")
    public List<Physic> getPhysicByAlias(@RequestParam String name, @RequestParam int status) {
        return physicService.findPhysicByAlias(name);
    }

    /**
     * 获取单位
     * @return
     */
    @GetMapping("/getUnit")
    public List<UnitEnum> getPhysicUnit() {
        return physicService.getAllUnit();
    }

}
