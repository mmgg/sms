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
import com.clwu.sms.service.SellingPricingService;
import com.clwu.sms.service.ShowApiDrugService;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.utils.StringUtil;
import com.clwu.sms.vo.PhysicBarcodeInfoVo;
import com.clwu.sms.vo.PhysicScanResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class PhysicServiceImpl implements PhysicService {

    private static final Logger log = LoggerFactory.getLogger(PhysicServiceImpl.class);


    @Autowired
    private PhysicMapper physicMapper;

    @Autowired
    private ShowApiDrugService showApiDrugService;

    @Autowired
    private SellingPricingService sellingPricingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPhysic(Physic physic) {
        if (null != physic) {
            validateSellingPrice(physic);
            normalizePhysic(physic);
            ensureBarcodeUnique(physic);
            physic.setStatus(StatusEnum.US_ENABLED.getCode());
            physic.setUpdateUser(1L);
            if (StringUtil.isBlank(physic.getSourceApi())) {
                physic.setSourceApi("MANUAL");
                physic.setDataVerified(1);
            } else {
                physic.setSourceSyncedAt(LocalDateTime.now());
                physic.setDataVerified(1);
            }
            physicMapper.insert(physic);
            saveSellingPrice(physic);
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
    @Transactional(rollbackFor = Exception.class)
    public void updPhysic(Physic physic) {
        if (physic.getId() != null && physic.getId() > 0L) {
            validateSellingPrice(physic);
            normalizePhysic(physic);
            ensureBarcodeUnique(physic);
            physic.setTenantId(null);
            physic.setDeleted(null);
            physicMapper.updateById(physic);
            saveSellingPrice(physic);
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
    public PhysicScanResultVo scanBarcode(String barcode) {
        Physic local = findPhysicByBarcode(barcode);
        if (local != null) {
            log.info("条码命中本地药品库: barcode={}, physicId={}", barcode, local.getId());
            return PhysicScanResultVo.builder().source("LOCAL").physic(local).build();
        }
        log.info("条码未命中本地药品库，调用外部接口: barcode={}", barcode);
        PhysicBarcodeInfoVo external = showApiDrugService.queryByBarcode(barcode);
        return PhysicScanResultVo.builder().source("REMOTE").external(external).build();
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

    private void normalizePhysic(Physic physic) {
        if (physic == null) {
            return;
        }
        physic.setBarcode(trimToNull(physic.getBarcode()));
        physic.setName(trimToNull(physic.getName()));
        physic.setSpec(trimToNull(physic.getSpec()));
        physic.setTrademark(trimToNull(physic.getTrademark()));
        physic.setManufacturer(trimToNull(physic.getManufacturer()));
        physic.setManufacturerAddress(trimToNull(physic.getManufacturerAddress()));
        physic.setApprovalNumber(trimToNull(physic.getApprovalNumber()));
        physic.setDosage(trimToNull(physic.getDosage()));
        physic.setIndications(trimToNull(physic.getIndications()));
        physic.setMainIngredients(trimToNull(physic.getMainIngredients()));
        physic.setContraindications(trimToNull(physic.getContraindications()));
        physic.setPrecautions(trimToNull(physic.getPrecautions()));
        physic.setStorageCondition(trimToNull(physic.getStorageCondition()));
        physic.setValidityPeriod(trimToNull(physic.getValidityPeriod()));
        physic.setCharacteristics(trimToNull(physic.getCharacteristics()));
        physic.setOtherNotes(trimToNull(physic.getOtherNotes()));
        physic.setNote(trimToNull(physic.getNote()));
        physic.setImageUrl(trimToNull(physic.getImageUrl()));
        physic.setAlias(trimToNull(physic.getAlias()));
        physic.setSourceApi(trimToNull(physic.getSourceApi()));
        physic.setSourcePayload(trimToNull(physic.getSourcePayload()));
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

    private String trimToNull(String value) {
        return StringUtil.isBlank(value) ? null : value.trim();
    }

    private void validateSellingPrice(Physic physic) {
        if (physic.getSellingPrice() == null
                || physic.getSellingPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "销售价必填且必须大于0");
        }
    }

    private void saveSellingPrice(Physic physic) {
        SellingPrice price = new SellingPrice();
        price.setPhysic(physic.getId());
        price.setPrice(physic.getSellingPrice());
        price.setStatus(StatusEnum.US_ENABLED.getCode());
        sellingPricingService.addSellingPrice(price);
    }
}
