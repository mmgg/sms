package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.PhysicTypeEnum;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.mapper.PhysicMapper;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhysicServiceImpl implements PhysicService {

    @Autowired
    private PhysicMapper physicMapper;

    @Override
    public void addPhysic(Physic physic) {
        if (null != physic) {
            physic.setStatus(StatusEnum.US_ENABLED.getCode());
            physic.setUpdateUser(1L);
            physicMapper.insert(physic);
        }
    }

    @Override
    public void delePhysic(Long physicId) {
        if (null == physicId || physicId <= 0L) return;
        UpdateWrapper<Physic> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", physicId).set("status", StatusEnum.US_DISABLE.getCode());
        physicMapper.update(null, updateWrapper);
    }

    @Override
    public void updPhysic(Physic physic) {
        if (physic.getId() != null && physic.getId() > 0L) {
            physicMapper.updateById(physic);
        }
    }

    @Override
    public Physic findPhysicById(Long pid) {
        if (null != pid && pid > 0L) {
            return physicMapper.selectById(pid);
        }
        return null;
    }

    @Override
    public List<Physic> findPhysicByName(String name) {
        QueryWrapper<Physic> qw = new QueryWrapper<>();
        //qw.ge("status", 0);
        if (StringUtil.isNotEmpty(name)) {
            qw.like("name", name);
        }
        return physicMapper.selectList(qw);
    }

    @Override
    public List<Physic> findPhysicByAlias(String alias) {
        QueryWrapper<Physic> qw = new QueryWrapper<>();
        qw.ge("status", 0);
        if (StringUtil.isNotEmpty(alias)) {
            qw.like("alias", alias);
        }
        return physicMapper.selectList(qw);
    }

    @Override
    public List<Physic> findPhysicByManufacturerString(String manufacturer) {
        QueryWrapper<Physic> qw = new QueryWrapper<>();
        qw.ge("status", 0);
        if (StringUtil.isNotEmpty(manufacturer)) {
            qw.like("manufacturer", manufacturer);
        }
        return physicMapper.selectList(qw);
    }

    @Override
    public List<UnitEnum> getAllUnit() {
        List<UnitEnum> unitEnums = new ArrayList<>();
        for (UnitEnum unitEnum : UnitEnum.values()) {
            unitEnums.add(unitEnum);
        }
        return unitEnums;
    }

    @Override
    public List<Physic> findAllPhysic() {
        QueryWrapper<Physic> qw = new QueryWrapper<>();
        //qw.ge("status", 0);
        return physicMapper.selectList(qw);
    }
}
