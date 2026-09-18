package com.clwu.sms.service;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.vo.PhysicScanResultVo;

import java.util.List;

public interface PhysicService {
    void addPhysic(Physic physic);
    void delePhysic(Long physicId);
    void updPhysic(Physic physic);
    Physic findPhysicById(Long pid);
    Physic findPhysicByBarcode(String barcode);
    PhysicScanResultVo scanBarcode(String barcode);
    List<Physic> findPhysicByName(String name);
    List<Physic> findPhysicByAlias(String alias);
    List<Physic> findPhysicByManufacturerString(String manufacturer);
    List<UnitEnum> getAllUnit();
    /** 查询所有药品/耗材 */
    List<Physic> findAllPhysic();
}
