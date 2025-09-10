package com.ventas.minipos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SaleDTO {
    private Long id;
    private LocalDateTime saleDate;
    private double total;

    private CustomerDTO customer;
    private String name;

    public SaleDTO(Long id, LocalDateTime saleDate, double total,
                   CustomerDTO customer, String name) {
        this.id = id;
        this.saleDate = saleDate;
        this.total = total;
        this.customer = customer;
        this.name = name;
    }


}