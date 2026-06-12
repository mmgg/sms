package com.clwu.sms.controller;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.mapper.PatientMapper;
import com.clwu.sms.mapper.PhysicMapper;
import com.clwu.sms.mapper.PrescriptionMapper;

import com.clwu.sms.vo.DashboardStatsVo;
import com.clwu.sms.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class DashboardController {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PhysicMapper physicMapper;

    @Autowired
    private PurchaseDetailMapper parchaseDetailMapper;

    @Autowired
    private PrescriptionMapper perscriptionMapper;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/patients")
    public String patients() {
        return "patient/list";
    }

    @GetMapping("/physic")
    public String physic() {
        return "physic/list";
    }

    @GetMapping("/purchase")
    public String purchase() {
        return "purchase/list";
    }

    @GetMapping("/prescription")
    public String prescription() {
        return "prescription/list";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory/list";
    }

    @GetMapping("/report")
    public String report() {
        return "report/index";
    }

    @GetMapping("/user")
    public String user() {
        return "user/list";
    }

    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ResultVo<DashboardStatsVo> stats() {
        long patientCount = patientMapper.selectCount(null);
        long physicCount = physicMapper.selectCount(null);

        // 统计有可用库存的药品种类数
        long stockItems = parchaseDetailMapper.selectStockSummary().size();

        // 本月处方数
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long monthPrescriptions = perscriptionMapper.selectMonthCount(monthStart);

        DashboardStatsVo stats = DashboardStatsVo.builder()
                .patients(patientCount)
                .physicCount(physicCount)
                .stockItems(stockItems)
                .monthPrescriptions(monthPrescriptions)
                .build();
        return ResultVo.ok(stats);
    }
}
