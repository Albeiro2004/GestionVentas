package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor

public class ListProductsDTO {
    private String nombre;
    private String id;
    private BigDecimal precioVenta;
    private String marca;
    private BigDecimal precioCompra;
    private Integer stock;
    private String location;
}
