package com.ventas.minipos.dto;

import lombok.Data;

@Data
public class SaleItemRequest {
    private String productId;
    private int cantidad;
    private double precioUnitario;
    private double descuento;
}
