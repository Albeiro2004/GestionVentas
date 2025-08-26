package com.ventas.minipos.web;

import com.ventas.minipos.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/Ventas/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public Map<String, BigDecimal> summary() {
        return Map.of(
                "totalPurchases", reportService.getTotalPurchases(),
                "totalSales", reportService.getTotalSales(),
                "totalProfit", reportService.getTotalProfit()
        );
    }
}
