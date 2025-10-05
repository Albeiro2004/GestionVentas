package com.ventas.minipos.web;

import com.ventas.minipos.domain.Debt;
import com.ventas.minipos.domain.Payment;
import com.ventas.minipos.dto.CustomerDebtsDTO;
import com.ventas.minipos.dto.DebtDTO;
import com.ventas.minipos.dto.PaymentDTO;
import com.ventas.minipos.repo.DebtRepository;
import com.ventas.minipos.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Ventas/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtRepository debtRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/all")
    public List<DebtDTO> getAllDebts() {
        List<Debt> debts = debtRepository.findAllWithPaymentsAndSaleAndCustomer();
        return debts.stream().map(d -> new DebtDTO(
                d.getId(),
                d.getTotalAmount(),
                d.getPendingAmount(),
                d.getPaid(),
                d.getSale() != null && d.getSale().getCustomer() != null
                        ? d.getSale().getCustomer().getNombre()
                        : (d.getServiceOrder() != null && d.getServiceOrder().getCustomer() != null
                        ? d.getServiceOrder().getCustomer().getNombre()
                        : "Cliente no disponible"),
                d.getDescription(),
                d.getPayments().stream()
                        .map(p -> new PaymentDTO(p.getId(), p.getAmount(), p.getPaymentDate()))
                        .toList()
        )).toList();
    }

    @GetMapping("/grouped")
    public List<CustomerDebtsDTO> getDebtsGroupedByCustomer() {
        List<Debt> debts = debtRepository.findAllWithPaymentsAndSaleAndCustomer();

        Map<String, List<Debt>> grouped = debts.stream()
                .collect(Collectors.groupingBy(
                        d -> {
                            String customerId;
                            String customerName;

                            if (d.getSale() != null && d.getSale().getCustomer() != null) {
                                customerId = String.valueOf(d.getSale().getCustomer().getDocumento());
                                customerName = d.getSale().getCustomer().getNombre();
                            } else if (d.getServiceOrder() != null && d.getServiceOrder().getCustomer() != null) {
                                customerId = String.valueOf(d.getServiceOrder().getCustomer().getDocumento());
                                customerName = d.getServiceOrder().getCustomer().getNombre();
                            } else {
                                customerId = "N/A";
                                customerName = "Cliente desconocido";
                            }

                            return customerId + "|" + customerName;
                        },
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<CustomerDebtsDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Debt>> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split("\\|", 2);
            String customerId = parts[0];
            String customerName = parts.length > 1 ? parts[1] : customerId;

            List<DebtDTO> debtDTOs = entry.getValue().stream().map(d -> {
                List<PaymentDTO> payments = d.getPayments().stream()
                        .map(p -> new PaymentDTO(p.getId(), p.getAmount(), p.getPaymentDate()))
                        .collect(Collectors.toList());

                // 👇 Lógica para asegurar que nunca sea null
                String debtCustomerName;
                if (d.getSale() != null && d.getSale().getCustomer() != null) {
                    debtCustomerName = d.getSale().getCustomer().getNombre();
                } else if (d.getServiceOrder() != null && d.getServiceOrder().getCustomer() != null) {
                    debtCustomerName = d.getServiceOrder().getCustomer().getNombre();
                } else {
                    debtCustomerName = "Cliente genérico";
                }

                return new DebtDTO(
                        d.getId(),
                        d.getTotalAmount(),
                        d.getPendingAmount(),
                        d.getPaid(),
                        debtCustomerName,
                        d.getDescription(),
                        payments
                );
            }).collect(Collectors.toList());

            double totalPending = debtDTOs.stream()
                    .mapToDouble(dto -> dto.pendingAmount() != null ? dto.pendingAmount() : 0.0)
                    .sum();

            result.add(new CustomerDebtsDTO(customerId, customerName, totalPending, debtDTOs));
        }

        return result;
    }

    @GetMapping("/{debtId}/payments")
    public List<PaymentDTO> getPaymentsByDebt(@PathVariable Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        return debt.getPayments().stream()
                .map(p -> new PaymentDTO(
                        p.getId(),
                        p.getAmount(),
                        p.getPaymentDate()
                ))
                .toList();
    }

    @GetMapping("/pending")
    public List<Map<String, Object>> getPendingDebts() {
        return debtRepository.findAllWithPaymentsAndSaleAndCustomer().stream()
                // filtrar por pendingAmount (evitar NPE)
                .filter(d -> d.getPendingAmount() != null && d.getPendingAmount() > 0)
                .map(d -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", d.getId());
                    dto.put("totalAmount", d.getTotalAmount() != null ? d.getTotalAmount() : 0.0);
                    dto.put("pendingAmount", d.getPendingAmount() != null ? d.getPendingAmount() : 0.0);

                    // nombre del cliente (si existe sale y customer)
                    String customerName = null;
                    if (d.getSale() != null && d.getSale().getCustomer() != null) {
                        customerName = d.getSale().getCustomer().getNombre();
                    }
                    dto.put("customerName", customerName);

                    // payments -> lista simple (id, amount, paymentDate)
                    List<Map<String,Object>> payments = Optional.ofNullable(d.getPayments())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(p -> {
                                Map<String,Object> pm = new HashMap<>();
                                pm.put("id", p.getId());
                                pm.put("amount", p.getAmount()); // si tu Payment usa otro nombre, cámbialo aquí
                                pm.put("paymentDate", p.getPaymentDate());
                                return pm;
                            })
                            .collect(Collectors.toList());
                    dto.put("payments", payments);

                    // opcionales: saleId, description, createAt...
                    dto.put("saleId", d.getSale() != null ? d.getSale().getId() : null);
                    dto.put("description", d.getDescription());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/{debtId}/payments")
    public PaymentDTO addPayment(@PathVariable Long debtId, @RequestBody PaymentDTO paymentRequest) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        Payment payment = new Payment();
        payment.setDebt(debt);
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentDate(
                paymentRequest.getPaymentDate() != null ? paymentRequest.getPaymentDate() : java.time.LocalDateTime.now()
        );

        paymentRepository.save(payment);

        // Actualizar saldo pendiente
        double newPending = (debt.getPendingAmount() != null ? debt.getPendingAmount() : 0.0) - payment.getAmount();
        debt.setPendingAmount(newPending);
        if (newPending <= 0) {
            debt.setPaid(true);
        }
        debtRepository.save(debt);

        return new PaymentDTO(payment.getId(), payment.getAmount(), payment.getPaymentDate());
    }

    @PutMapping("/cancel/{documento}")
    public ResponseEntity<String> cancelAllDebtsByCustomer(@PathVariable String documento) {
        List<Debt> debts = debtRepository.findBySale_Customer_Documento(documento);

        if (debts.isEmpty()) {
            return ResponseEntity.badRequest().body("El cliente no tiene deudas.");
        }

        debts.forEach(debt -> {
            debt.setPendingAmount(0.0);
            debt.setPaid(true);
        });
        debtRepository.saveAll(debts);

        return ResponseEntity.ok("Todas las deudas del cliente han sido canceladas.");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDeudasPendientes() {
        Long count = debtRepository.countByPaidFalse();
        return ResponseEntity.ok(count);
    }



}
