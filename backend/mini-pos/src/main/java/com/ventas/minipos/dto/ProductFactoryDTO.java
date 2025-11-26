package com.ventas.minipos.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductFactoryDTO {
    private String productId;
    private String nombre;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private String marca;
}
