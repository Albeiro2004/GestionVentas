package com.ventas.minipos.service;

import com.ventas.minipos.dto.DebtDTO;
import com.ventas.minipos.dto.PaymentDTO;
import com.ventas.minipos.repo.DebtRepository;

import java.util.List;

public class DebtService {
    private DebtRepository debtRepository;
    public List<DebtDTO> getAllDebts() {
        return debtRepository.findAllWithPaymentsAndSaleAndCustomer()
                .stream()
                .map(d -> new DebtDTO(
                        d.getId(),
                        d.getTotalAmount(),
                        d.getPendingAmount(),
                        d.getPaid(),
                        d.getSale().getCustomer().getNombre(),
                        d.getPayments().stream()
                                .map(p -> new PaymentDTO(p.getId(), p.getAmount(), p.getPaymentDate()))
                                .toList()
                ))
                .toList();
    }

}
