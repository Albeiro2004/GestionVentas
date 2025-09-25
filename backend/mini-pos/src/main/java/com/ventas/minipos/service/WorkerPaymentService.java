package com.ventas.minipos.service;

import com.ventas.minipos.domain.ServiceOrder;
import com.ventas.minipos.domain.Worker;
import com.ventas.minipos.domain.WorkerPayment;
import com.ventas.minipos.repo.ServiceOrderRepository;
import com.ventas.minipos.repo.WorkerPaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerPaymentService {

    private final WorkerPaymentRepository paymentRepo;
    private final ServiceOrderRepository orderRepo;

    // Calcular cuánto se le debe al trabajador
    public double calculatePending(Worker worker) {
        List<ServiceOrder> orders = orderRepo.findAll()
                .stream()
                .filter(o -> o.getWorker().getId().equals(worker.getId()))
                .toList();

        double totalOrders = orders.stream().mapToDouble(ServiceOrder::getWorkerShare).sum();
        double totalPaid = paymentRepo.findAll().stream()
                .filter(p -> p.getWorker().getId().equals(worker.getId()))
                .mapToDouble(WorkerPayment::getAmount)
                .sum();

        return totalOrders - totalPaid;
    }

    // Registrar un pago parcial o total
    @Transactional
    public WorkerPayment payWorker(Worker worker, double amount) {
        double pending = calculatePending(worker);
        if (amount > pending) {
            throw new IllegalArgumentException("El monto a pagar excede lo pendiente. Pendiente: " + pending);
        }

        WorkerPayment payment = new WorkerPayment();
        payment.setWorker(worker);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepo.save(payment);
    }
}
