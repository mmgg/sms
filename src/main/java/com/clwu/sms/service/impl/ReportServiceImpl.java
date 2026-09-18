package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.entity.*;
import com.clwu.sms.enums.PhysicTypeEnum;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PrescriptionPhysicMapper;
import com.clwu.sms.mapper.PrescriptionMapper;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.*;
import com.clwu.sms.vo.*;
import com.clwu.sms.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private PrescriptionPhysicMapper prescriptionPhysicMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Autowired
    private PhysicService physicService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PatientService patientService;

    private List<PrescriptionPhysic> getFilteredPP(String startTime, String endTime) {
        QueryWrapper<PrescriptionPhysic> qw = new QueryWrapper<>();
        qw.eq("status", StatusEnum.US_ENABLED.getCode());
        if (StringUtil.isNotEmpty(startTime) || StringUtil.isNotEmpty(endTime)) {
            QueryWrapper<Prescription> pqw = new QueryWrapper<>();
            if (StringUtil.isNotEmpty(startTime)) {
                pqw.ge("create_time", LocalDateTime.parse(startTime));
            }
            if (StringUtil.isNotEmpty(endTime)) {
                pqw.le("create_time", LocalDateTime.parse(endTime));
            }
            List<Prescription> prescriptions = prescriptionMapper.selectList(pqw);
            if (prescriptions.isEmpty()) return new ArrayList<>();
            Set<Long> ids = prescriptions.stream().map(Prescription::getId).collect(Collectors.toSet());
            qw.in("prescription", ids);
        }
        return prescriptionPhysicMapper.selectList(qw);
    }

    @Override
    public List<ProfitByDrugVo> getProfitByDrug(String startTime, String endTime) {
        log.info("查询药品利润报表: start={}, end={}", startTime, endTime);
        List<PrescriptionPhysic> list = getFilteredPP(startTime, endTime);
        if (list.isEmpty()) return new ArrayList<>();

        Map<Long, List<PrescriptionPhysic>> grouped = list.stream()
                .filter(pp -> pp.getIncome() != null && pp.getCost() != null)
                .collect(Collectors.groupingBy(PrescriptionPhysic::getPhysic));

        List<ProfitByDrugVo> result = new ArrayList<>();
        for (Map.Entry<Long, List<PrescriptionPhysic>> entry : grouped.entrySet()) {
            Long physicId = entry.getKey();
            Physic physic = physicService.findPhysicById(physicId);
            List<PrescriptionPhysic> items = entry.getValue();

            BigDecimal profit = items.stream()
                    .map(pp -> pp.getIncome().subtract(pp.getCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int quantity = items.stream().mapToInt(pp -> pp.getNum() != null ? pp.getNum() : 0).sum();

            ProfitByDrugVo vo = ProfitByDrugVo.builder()
                    .physicId(physicId)
                    .physicName(physic != null ? physic.getName() : "未知")
                    .profit(profit)
                    .quantity(quantity)
                    .typeName(resolvePhysicTypeName(physic))
                    .build();
            result.add(vo);
        }
        result.sort((a, b) -> b.getProfit().compareTo(a.getProfit()));
        return result;
    }

    @Override
    public List<ProfitByPrescriptionVo> getProfitByPrescription(String startTime, String endTime) {
        log.info("查询处方利润报表: start={}, end={}", startTime, endTime);
        List<PrescriptionPhysic> list = getFilteredPP(startTime, endTime);
        if (list.isEmpty()) return new ArrayList<>();

        Map<Long, List<PrescriptionPhysic>> grouped = list.stream()
                .filter(pp -> pp.getIncome() != null && pp.getCost() != null)
                .collect(Collectors.groupingBy(PrescriptionPhysic::getPrescription));

        List<ProfitByPrescriptionVo> result = new ArrayList<>();
        for (Map.Entry<Long, List<PrescriptionPhysic>> entry : grouped.entrySet()) {
            Long prescriptionId = entry.getKey();
            Prescription p = prescriptionService.findPrescriptionById(prescriptionId);
            if (p == null) continue;
            Patient patient = patientService.findPatientById(p.getPatient());
            List<PrescriptionPhysic> items = entry.getValue();

            BigDecimal profit = items.stream()
                    .map(pp -> pp.getIncome().subtract(pp.getCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ProfitByPrescriptionVo vo = ProfitByPrescriptionVo.builder()
                    .prescriptionId(prescriptionId)
                    .patientName(patient != null ? patient.getName() : "未知")
                    .profit(profit)
                    .createTime(p.getCreateTime() != null ? p.getCreateTime().toString() : "-")
                    .itemCount(items.size())
                    .build();
            result.add(vo);
        }
        result.sort((a, b) -> b.getProfit().compareTo(a.getProfit()));
        return result;
    }

    @Override
    public List<SalesByDrugVo> getSalesByDrug(String startTime, String endTime) {
        log.info("查询药品销量报表: start={}, end={}", startTime, endTime);
        List<PrescriptionPhysic> list = getFilteredPP(startTime, endTime);
        if (list.isEmpty()) return new ArrayList<>();

        Map<Long, List<PrescriptionPhysic>> grouped = list.stream()
                .collect(Collectors.groupingBy(PrescriptionPhysic::getPhysic));

        List<SalesByDrugVo> result = new ArrayList<>();
        for (Map.Entry<Long, List<PrescriptionPhysic>> entry : grouped.entrySet()) {
            Long physicId = entry.getKey();
            Physic physic = physicService.findPhysicById(physicId);
            int quantity = entry.getValue().stream()
                    .mapToInt(pp -> pp.getNum() != null ? pp.getNum() : 0).sum();

            SalesByDrugVo vo = SalesByDrugVo.builder()
                    .physicId(physicId)
                    .physicName(physic != null ? physic.getName() : "未知")
                    .quantity(quantity)
                    .typeName(resolvePhysicTypeName(physic))
                    .build();
            result.add(vo);
        }
        result.sort((a, b) -> b.getQuantity().compareTo(a.getQuantity()));
        return result;
    }

    @Override
    public List<SalesByTypeVo> getSalesByType(String startTime, String endTime) {
        log.info("查询类型销量报表: start={}, end={}", startTime, endTime);
        List<SalesByDrugVo> drugSales = getSalesByDrug(startTime, endTime);
        Map<String, Integer> typeMap = new HashMap<>();
        for (SalesByDrugVo vo : drugSales) {
            typeMap.merge(vo.getTypeName(), vo.getQuantity(), Integer::sum);
        }

        List<SalesByTypeVo> result = new ArrayList<>();
        int idx = 1;
        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            SalesByTypeVo vo = SalesByTypeVo.builder()
                    .type(idx)
                    .typeName(entry.getKey())
                    .quantity(entry.getValue())
                    .build();
            result.add(vo);
            idx++;
        }
        return result;
    }

    @Override
    public List<StockSummaryVo> getInventoryByDrug(String startTime, String endTime) {
        log.info("查询库存报表: start={}, end={}", startTime, endTime);
        // Inventory is current state, time range not applicable
        return purchaseDetailMapper.selectStockSummary();
    }

    @Override
    public List<InventoryStatusVo> getInventoryStatus(String startTime, String endTime) {
        log.info("查询库存状态报表: start={}, end={}", startTime, endTime);
        // Inventory is current state, time range not applicable
        QueryWrapper<PurchaseDetail> qw = new QueryWrapper<>();
        qw.isNotNull("status");
        List<PurchaseDetail> list = purchaseDetailMapper.selectList(qw);

        Map<Integer, Long> statusMap = new HashMap<>();
        for (PurchaseDetail pd : list) {
            int status = pd.getStatus();
            statusMap.merge(status, 1L, Long::sum);
        }

        List<InventoryStatusVo> result = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : statusMap.entrySet()) {
            String name = resolveInventoryStatusName(entry.getKey());
            InventoryStatusVo vo = InventoryStatusVo.builder()
                    .status(entry.getKey())
                    .statusName(name)
                    .count(entry.getValue())
                    .build();
            result.add(vo);
        }
        return result;
    }

    private String resolvePhysicTypeName(Physic physic) {
        if (physic == null) {
            return "未知";
        }
        PhysicTypeEnum type = PhysicTypeEnum.findByCode(physic.getType());
        return type == null ? "未知" : type.getDesc();
    }

    private String resolveInventoryStatusName(int status) {
        if (status == StatusEnum.US_ENABLED.getCode()) {
            return StatusEnum.US_ENABLED.getDesc();
        }
        if (status == StatusEnum.US_OCCUPY.getCode()) {
            return StatusEnum.US_OCCUPY.getDesc();
        }
        return "其他";
    }
}
