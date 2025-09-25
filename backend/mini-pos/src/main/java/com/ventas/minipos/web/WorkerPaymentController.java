package com.ventas.minipos.web;

import com.ventas.minipos.domain.Worker;
import com.ventas.minipos.domain.WorkerPayment;
import com.ventas.minipos.dto.PayWorkerRequest;
import com.ventas.minipos.repo.WorkerPaymentRepository;
import com.ventas.minipos.repo.WorkerRepository;
import com.ventas.minipos.service.PaymentService;
import com.ventas.minipos.service.WorkerPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Ventas/payments")
@RequiredArgsConstructor
public class WorkerPaymentController {

    private final WorkerPaymentService paymentService;
    private final WorkerRepository workerRepo;
    private final WorkerPaymentRepository paymentRepository;
    private final PaymentService paymentSer;

    @PostMapping("/pay")
    public ResponseEntity<?> payWorker(@RequestBody PayWorkerRequest request) {
        Worker worker = workerRepo.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        double amount = request.getAmount();

        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor a cero");
        }

        WorkerPayment payment = paymentService.payWorker(worker, request.getAmount());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/pending/{workerId}")
    public ResponseEntity<?> getPending(@PathVariable Long workerId) {
        Worker worker = workerRepo.findById(workerId).orElseThrow();
        double pending = paymentService.calculatePending(worker);
        return ResponseEntity.ok(Map.of("workerId", workerId, "pending", pending));
    }

    @GetMapping("/history/{workerId}")
    public ResponseEntity<List<WorkerPayment>> getPaymentHistory(@PathVariable Long workerId) {
        Worker worker = workerRepo.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado"));

        List<WorkerPayment> history = paymentRepository.findByWorker(worker);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<Map<String, Double>> getWorkerSummary(@PathVariable Long workerId) {
        Worker worker = workerRepo.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado"));

        double earned = paymentSer.calculateTotalEarned(worker);
        double paid = paymentSer.calculateTotalPaid(worker);
        double pending = earned - paid;

        return ResponseEntity.ok(
                Map.of(
                        "earned", earned,
                        "paid", paid,
                        "pending", pending
                )
        );
    }

}
