package com.ventas.minipos.dto;

import java.util.List;

public record DebtDTO(
        Long id,
        Double totalAmount,
        Double pendingAmount,
        Boolean paid,
        String customerName,
        List<PaymentDTO> payments

) {}
