package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Worker;
import com.ventas.minipos.domain.WorkerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkerPaymentRepository extends JpaRepository<WorkerPayment, Long> {

    // Histórico de pagos de un trabajador
    List<WorkerPayment> findByWorker(Worker worker);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM WorkerPayment p WHERE p.worker = :worker")
    double getTotalPaidByWorker(@Param("worker") Worker worker);

}
