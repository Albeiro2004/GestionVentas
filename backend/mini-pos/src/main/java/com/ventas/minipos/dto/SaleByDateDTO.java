package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SaleByDateDTO {
    private LocalDateTime saleDate;
    private Double totalAmount;

    public SaleByDateDTO(LocalDateTime saleDate, Double totalAmount) {
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
    }


}
