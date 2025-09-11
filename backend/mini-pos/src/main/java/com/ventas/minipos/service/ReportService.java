package com.ventas.minipos.service;

import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.domain.Sale;
import com.ventas.minipos.dto.FullReportDTO;
import com.ventas.minipos.repo.PurchaseRepository;
import com.ventas.minipos.repo.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final PurchaseRepository purchaseRepository;
    private final SaleRepository saleRepository;

    public FullReportDTO generateFullReport(LocalDateTime start, LocalDateTime end) {

        Double totalSales = Optional.ofNullable(
                saleRepository.findTotalSalesByDateRange(start, end)
        ).orElse(0.0);

        Double totalPurchases = Optional.ofNullable(
                purchaseRepository.findTotalPurchasesByDateRange(start, end)
        ).orElse(0.0);

        Double totalProfit = totalSales - totalPurchases;

        return new FullReportDTO(
                totalSales,
                totalProfit,
                totalPurchases,
                saleRepository.findTopSellingProducts(start, end),
                saleRepository.findSalesByDateRange(start, end)
        );
    }

    public ReportService(PurchaseRepository purchaseRepository,
                         SaleRepository saleRepository) {
        this.purchaseRepository = purchaseRepository;
        this.saleRepository = saleRepository;
    }

    public BigDecimal getTotalPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return purchases.stream()
                .flatMap(p -> p.getItems().stream())
                // FIX: Convierte el int a BigDecimal
                .map(item -> item.getCostoUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .flatMap(s -> s.getItems().stream())
                // FIX: Convierte el int a BigDecimal
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalProfit() {
        return getTotalSales().subtract(getTotalPurchases());
    }
}