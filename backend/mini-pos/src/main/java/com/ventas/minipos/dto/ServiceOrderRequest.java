package com.ventas.minipos.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceOrderRequest {
    private String customerId;
    private Long workerId;
    private String description;
    private Double laborCost;
    private String paymentType;   // CASH, ABONO, DEBT
    private Double abonoAmount;   // opcional
    private List<ProductDTO> products = new ArrayList<>();
}

