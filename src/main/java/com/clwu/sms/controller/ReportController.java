package com.clwu.sms.controller;

import com.clwu.sms.service.ReportService;
import com.clwu.sms.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/profit-by-drug")
    public List<ProfitByDrugVo> profitByDrug(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getProfitByDrug(startTime, endTime);
    }

    @GetMapping("/profit-by-prescription")
    public List<ProfitByPrescriptionVo> profitByPrescription(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getProfitByPrescription(startTime, endTime);
    }

    @GetMapping("/sales-by-drug")
    public List<SalesByDrugVo> salesByDrug(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getSalesByDrug(startTime, endTime);
    }

    @GetMapping("/sales-by-type")
    public List<SalesByTypeVo> salesByType(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getSalesByType(startTime, endTime);
    }

    @GetMapping("/inventory-by-drug")
    public List<StockSummaryVo> inventoryByDrug(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getInventoryByDrug(startTime, endTime);
    }

    @GetMapping("/inventory-status")
    public List<InventoryStatusVo> inventoryStatus(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getInventoryStatus(startTime, endTime);
    }

    @GetMapping("/export")
    public void export(@RequestParam String type,
                       @RequestParam(required = false) String startTime,
                       @RequestParam(required = false) String endTime,
                       HttpServletResponse response) throws Exception {
        String filename;
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case "profitByDrug": {
                filename = "profit-drug.csv";
                sb.append("Drug Name,Type,Sales,Profit\n");
                List<ProfitByDrugVo> list = reportService.getProfitByDrug(startTime, endTime);
                for (ProfitByDrugVo v : list) {
                    sb.append(v.getPhysicName()).append(",")
                      .append(v.getTypeName()).append(",")
                      .append(v.getQuantity()).append(",")
                      .append(v.getProfit()).append("\n");
                }
                break;
            }
            case "profitByPrescription": {
                filename = "profit-prescription.csv";
                sb.append("Rx ID,Patient,Items,Profit,Date\n");
                List<ProfitByPrescriptionVo> list = reportService.getProfitByPrescription(startTime, endTime);
                for (ProfitByPrescriptionVo v : list) {
                    sb.append(v.getPrescriptionId()).append(",")
                      .append(v.getPatientName()).append(",")
                      .append(v.getItemCount()).append(",")
                      .append(v.getProfit()).append(",")
                      .append(v.getCreateTime()).append("\n");
                }
                break;
            }
            case "salesByDrug": {
                filename = "sales-drug.csv";
                sb.append("Drug Name,Type,Sales\n");
                List<SalesByDrugVo> list = reportService.getSalesByDrug(startTime, endTime);
                for (SalesByDrugVo v : list) {
                    sb.append(v.getPhysicName()).append(",")
                      .append(v.getTypeName()).append(",")
                      .append(v.getQuantity()).append("\n");
                }
                break;
            }
            case "inventory": {
                filename = "inventory.csv";
                sb.append("Drug Name,Available,Occupied\n");
                List<StockSummaryVo> list = reportService.getInventoryByDrug(startTime, endTime);
                for (StockSummaryVo v : list) {
                    sb.append(v.getPhysicName()).append(",")
                      .append(v.getAvailableQty()).append(",")
                      .append(v.getOccupiedQty()).append("\n");
                }
                break;
            }
            default:
                return;
        }

        response.setContentType("text/csv;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedName + "\"");
        try (OutputStream os = response.getOutputStream()) {
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }
}
