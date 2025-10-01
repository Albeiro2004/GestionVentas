package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    private String id;
    private String nombre;
    private String marca;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;

    private String location;
    private Integer stock;
}
