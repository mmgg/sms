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

/**
 * @Author: wuchunlong
 * @Date: 2025/9/25 08:50
 * @Description:
 **/
@Service
public class PhysicServiceImpl implements PhysicService {
    @Autowired
    private PhysicMapper physicMapper;
    /**
     * 增加药品or耗材
     *
     * @param physic
     */
    @Override
    public void addPhysic(Physic physic) {
        if (null != physic) {
            physic.setStatus(StatusEnum.US_INIT.getCode());
            physic.setUpdateUser(1L);
            physicMapper.insert(physic);
        }
    }

    /**
     * 删除
     *
     * @param physicId
     */
    @Override
    public void delePhysic(Long physicId) {
        if (null == physicId || physicId <= 0L) {
            return;
        }
        UpdateWrapper<Physic> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", physicId).set("status", StatusEnum.US_DISABLE.getCode());
        physicMapper.update(null,updateWrapper);
    }

    /**
     * 修改药品or耗材
     *
     * @param physic
     */
    @Override
    public void updPhysic(Physic physic) {
        UpdateWrapper<Physic> updateWrapper = new UpdateWrapper<>();
        if(physic.getId()!=null && physic.getId().compareTo(0L) > 0) {
            updateWrapper.set("id", physic.getId());
            return;
        }
        physicMapper.update(physic, updateWrapper);

    }

    /**
     * 基于ID查找
     *
     * @param pid 药品ID
     * @return
     */
    @Override
    public Physic findPhysicById(Long pid) {
        if(null != pid && pid.compareTo(0L) > 0) {
            return physicMapper.selectById(pid);
        }
        return null;
    }

    /**
     * 基于名称查找药物
     *
     * @param name 药品名称
     * @return 满足条件列表
     */
    @Override
    public List<Physic> findPhysicByName(String name) {
        if (StringUtil.isNotEmpty(name)) {
            QueryWrapper<Physic> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("name", name).eq("status", StatusEnum.US_ENABLED.getCode());
            return physicMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 基于别名查找药物
     *
     * @param alias 别名
     * @return 满足条件列表
     */
    @Override
    public List<Physic> findPhysicByAlias(String alias) {
        if (StringUtil.isNotEmpty(alias)) {
            QueryWrapper<Physic> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("alias", alias).eq("status", StatusEnum.US_ENABLED.getCode());
            return physicMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 基于生产厂家查找
     *
     * @param manufacturer
     * @return
     */
    @Override
    public List<Physic> findPhysicByManufacturerString(String manufacturer) {
        if (StringUtil.isNotEmpty(manufacturer)) {
            QueryWrapper<Physic> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("manufacturer", manufacturer).eq("status", StatusEnum.US_ENABLED.getCode());
            return physicMapper.selectList(queryWrapper);
        }
        return null;
    }

    /**
     * 获取药品或者耗材单位
     *
     * @return
     */
    @Override
    public List<UnitEnum> getAllUnit() {
        List<UnitEnum> unitEnums = new ArrayList<>();
        for(UnitEnum unitEnum: UnitEnum.values()) {
            unitEnums.add(unitEnum);
        }
        return unitEnums;
    }
}
