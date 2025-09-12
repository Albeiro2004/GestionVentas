package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopProductDTO {
    private String productName;
    private Long quantitySold;
    private BigDecimal costo;
    private BigDecimal price;

}
