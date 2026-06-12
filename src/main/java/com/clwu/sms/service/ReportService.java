package com.clwu.sms.service;

import com.clwu.sms.vo.*;

import java.util.List;

public interface ReportService {
    List<ProfitByDrugVo> getProfitByDrug(String startTime, String endTime);
    List<ProfitByPrescriptionVo> getProfitByPrescription(String startTime, String endTime);
    List<SalesByDrugVo> getSalesByDrug(String startTime, String endTime);
    List<SalesByTypeVo> getSalesByType(String startTime, String endTime);
    List<StockSummaryVo> getInventoryByDrug(String startTime, String endTime);
    List<InventoryStatusVo> getInventoryStatus(String startTime, String endTime);
}
