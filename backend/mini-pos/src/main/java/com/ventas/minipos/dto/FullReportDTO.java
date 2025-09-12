package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullReportDTO {
    private Double totalSales;
    private Double totalProfit;
    private Double totalPurchases;
    private List<TopProductDTO> topSellingProducts;
    private List<SaleByDateDTO> salesByDateRange;
    private List<SaleDTO> sales;

    private Long totalProducts;
    private Long lowStockProducts;
    private Long outOfStockProducts;
    private Double inventoryValue;
}
