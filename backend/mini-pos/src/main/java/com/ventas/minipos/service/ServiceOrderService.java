package com.ventas.minipos.service;

import com.ventas.minipos.domain.*;
import com.ventas.minipos.dto.ServiceOrderRequest;
import com.ventas.minipos.repo.CustomerRepository;
import com.ventas.minipos.repo.DebtRepository;
import com.ventas.minipos.repo.ServiceOrderRepository;
import com.ventas.minipos.repo.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository orderRepo;
    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;

    @Transactional
    public ServiceOrder registerService(ServiceOrderRequest request) {
        // 🔍 Validar cliente
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // 🚫 Restricción: cliente genérico NO puede tener abonos ni deudas
        if (isGenericCustomer(customer) &&
                ("ABONO".equals(request.getPaymentType()) || "DEBT".equals(request.getPaymentType()))) {
            throw new IllegalArgumentException("El cliente genérico no puede tener abonos ni deudas. Solo pagos de contado.");
        }

        // 🔍 Validar mecánico
        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Mecánico no encontrado"));

        // 📝 Crear ServiceOrder
        ServiceOrder order = new ServiceOrder();
        order.setCustomer(customer);
        order.setWorker(worker);
        order.setDescription(request.getDescription());
        order.setTotalPrice(request.getTotalPrice());
        order.setWorkerShare(request.getTotalPrice() * 0.7);
        order.setWorkshopShare(request.getTotalPrice() * 0.3);
        order.setServiceDate(LocalDateTime.now());

        ServiceOrder savedOrder = orderRepo.save(order);

        // 📌 Manejo de deuda si es ABONO o DEBT
        if ("ABONO".equals(request.getPaymentType()) || "DEBT".equals(request.getPaymentType())) {
            double abono = request.getAbonoAmount() != null ? request.getAbonoAmount() : 0.0;
            double pendingAmount = request.getTotalPrice() - abono;

            Debt debt = Debt.builder()
                    .serviceOrder(savedOrder)
                    .totalAmount(request.getTotalPrice())
                    .createAt(LocalDateTime.now())
                    .pendingAmount(pendingAmount)
                    .paid(pendingAmount <= 0)
                    .description("ABONO".equals(request.getPaymentType())
                            ? "Servicio fiado. Abono inicial de " + abono
                            : "Servicio fiado. Sin abono inicial")
                    .build();

            if (abono > 0) {
                Payment payment = Payment.builder()
                        .debt(debt)
                        .amount(abono)
                        .paymentDate(LocalDateTime.now())
                        .build();
                debt.getPayments().add(payment);
            }

            debtRepository.save(debt);
        }

        return savedOrder;
    }

    // 🔎 Helper para identificar cliente genérico
    private boolean isGenericCustomer(Customer customer) {
        return "0000000000".equals(customer.getDocumento());
    }
}

