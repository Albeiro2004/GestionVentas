package com.ventas.minipos.service;

import com.ventas.minipos.domain.Debt;
import com.ventas.minipos.domain.Payment;
import com.ventas.minipos.repo.DebtRepository;
import com.ventas.minipos.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Debt registerPayment(Long debtId, Double amount) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Deuda no encontrada"));

        Payment payment = Payment.builder()
                .amount(amount)
                .build();

        debt.addPayment(payment);

        // Persistir
        paymentRepository.save(payment);
        return debtRepository.save(debt);
    }
}
