package com.ventas.minipos.repo;

import com.ventas.minipos.domain.ServiceOrder;
import com.ventas.minipos.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    // Total generado por un trabajador
    @Query("SELECT COALESCE(SUM(o.workerShare), 0) FROM ServiceOrder o WHERE o.worker = :worker")
    double getTotalEarnedByWorker(@Param("worker") Worker worker);
}
