package com.ventas.minipos.dto;

import com.ventas.minipos.domain.PaymentType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data // Genera getters, setters, toString(), equals() y hashCode()
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class SaleRequest {

    private String customerId;
    private PaymentType paymentType;
    private Double abonoAmount;
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String productId;
        private int cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
    }
}