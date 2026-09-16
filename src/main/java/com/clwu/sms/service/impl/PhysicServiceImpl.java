package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.enums.PhysicTypeEnum;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.enums.UnitEnum;
import com.clwu.sms.mapper.PhysicMapper;
import com.clwu.sms.service.PhysicService;
import com.clwu.sms.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhysicServiceImpl implements PhysicService {

    private static final Logger log = LoggerFactory.getLogger(PhysicServiceImpl.class);


    @Autowired
    private PhysicMapper physicMapper;

    @Override
    public void addPhysic(Physic physic) {
        if (null != physic) {
            normalizeBarcode(physic);
            ensureBarcodeUnique(physic);
            physic.setStatus(StatusEnum.US_ENABLED.getCode());
            physic.setUpdateUser(1L);
            physicMapper.insert(physic);
            log.info("新增药品/耗材: physicId={}, name={}, barcode={}",
                    physic.getId(), physic.getName(), physic.getBarcode());
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void delePhysic(Long physicId) {
        if (null == physicId || physicId <= 0L) return;
        physicMapper.deleteById(physicId);
        log.info("逻辑删除药品/耗材: physicId={}", physicId);
    }

    @Override
    public void updPhysic(Physic physic) {
        if (physic.getId() != null && physic.getId() > 0L) {
            normalizeBarcode(physic);
            ensureBarcodeUnique(physic);
            physic.setTenantId(null);
            physic.setDeleted(null);
            physicMapper.updateById(physic);
            log.info("更新药品/耗材: physicId={}, name={}, barcode={}",
                    physic.getId(), physic.getName(), physic.getBarcode());
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
    public Physic findPhysicByBarcode(String barcode) {
        if (StringUtil.isBlank(barcode)) {
            return null;
        }
        QueryWrapper<Physic> wrapper = new QueryWrapper<>();
        wrapper.eq("barcode", barcode.trim());
        return physicMapper.selectOne(wrapper);
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

    private void normalizeBarcode(Physic physic) {
        if (physic == null || StringUtil.isBlank(physic.getBarcode())) {
            if (physic != null) {
                physic.setBarcode(null);
            }
            return;
        }
        physic.setBarcode(physic.getBarcode().trim());
    }

    private void ensureBarcodeUnique(Physic physic) {
        if (physic == null || StringUtil.isBlank(physic.getBarcode())) {
            return;
        }
        QueryWrapper<Physic> wrapper = new QueryWrapper<>();
        wrapper.eq("barcode", physic.getBarcode());
        if (physic.getId() != null && physic.getId() > 0L) {
            wrapper.ne("id", physic.getId());
        }
        if (physicMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "条码已存在: " + physic.getBarcode());
        }
    }
}
