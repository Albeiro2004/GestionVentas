package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDebtsDTO {
    private String customerId;      // documento / id del cliente (string si tu Customer.documento es string)
    private String customerName;
    private Double totalPending;    // suma pendiente por cliente (opcional)
    private List<DebtDTO> debts;
}
