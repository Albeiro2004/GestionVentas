package com.ventas.minipos.service;

import com.ventas.minipos.domain.Debt;
import com.ventas.minipos.domain.Payment;
import com.ventas.minipos.domain.Worker;
import com.ventas.minipos.repo.DebtRepository;
import com.ventas.minipos.repo.PaymentRepository;
import com.ventas.minipos.repo.ServiceOrderRepository;
import com.ventas.minipos.repo.WorkerPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private final WorkerPaymentRepository paymentRepo;
    private final ServiceOrderRepository orderRepo;

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

    public double calculateTotalEarned(Worker worker) {
        return orderRepo.getTotalEarnedByWorker(worker);
    }

    public double calculateTotalPaid(Worker worker) {
        return paymentRepo.getTotalPaidByWorker(worker);
    }

    public double calculatePending(Worker worker) {
        return calculateTotalEarned(worker) - calculateTotalPaid(worker);
    }
}
