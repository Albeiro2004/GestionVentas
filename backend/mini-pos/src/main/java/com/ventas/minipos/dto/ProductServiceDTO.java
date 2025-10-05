package com.ventas.minipos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductServiceDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal subtotal;
}
