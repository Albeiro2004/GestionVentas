package com.ventas.minipos.web;

import com.ventas.minipos.dto.FullReportDTO;
import com.ventas.minipos.dto.SaleByDateDTO;
import com.ventas.minipos.repo.SaleRepository;
import com.ventas.minipos.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Ventas/reports")
public class ReportController {

    private final ReportService reportService;
    private final SaleRepository saleRepository;

    public ReportController(ReportService reportService, SaleRepository saleRepository) {
        this.reportService = reportService;
        this.saleRepository = saleRepository;
    }

    @GetMapping("/summary")
    public Map<String, BigDecimal> summary() {
        return Map.of(
                "totalPurchases", reportService.getTotalPurchases(),
                "totalSales", reportService.getTotalSales(),
                "totalProfit", reportService.getTotalProfit()
        );
    }

    @GetMapping("/full-report")
    public ResponseEntity<FullReportDTO> getFullReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        FullReportDTO report = reportService.generateFullReport(startDate.atStartOfDay(), endDate.atTime(23,59,59));
        return ResponseEntity.ok(report);
    }
}


