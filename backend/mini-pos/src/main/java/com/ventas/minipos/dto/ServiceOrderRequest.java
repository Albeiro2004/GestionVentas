package com.ventas.minipos.dto;

import lombok.Data;

@Data
public class ServiceOrderRequest {
    private String customerId;
    private Long workerId;
    private String description;
    private Double totalPrice;
    private String paymentType;   // CASH, ABONO, DEBT
    private Double abonoAmount;   // opcional
}

