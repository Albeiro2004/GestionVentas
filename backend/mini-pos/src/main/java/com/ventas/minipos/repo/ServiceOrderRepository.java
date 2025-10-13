package com.ventas.minipos.repo;

import com.ventas.minipos.domain.ServiceOrder;
import com.ventas.minipos.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    // Total generado por un trabajador
    @Query("SELECT COALESCE(SUM(o.workerShare), 0) FROM ServiceOrder o WHERE o.worker = :worker")
    double getTotalEarnedByWorker(@Param("worker") Worker worker);

    @Query("""
    SELECT DISTINCT s FROM ServiceOrder s
    LEFT JOIN FETCH s.products sp
    LEFT JOIN FETCH sp.product
    LEFT JOIN FETCH s.customer
    WHERE s.serviceDate BETWEEN :startDateTime AND :endDateTime
      AND (:workerId IS NULL OR s.worker.id = :workerId)
      AND (:customerId IS NULL OR s.customer.documento = :customerId)
    ORDER BY s.serviceDate DESC
    """)
    List<ServiceOrder> findFiltered(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("workerId") Long workerId, @Param("customerId") String customerId
    );

    @Query(value = "SELECT EXTRACT(MONTH FROM s.service_date) AS mes, SUM(s.workshop_share) AS total " +
            "FROM service_order s " +
            "WHERE s.service_date BETWEEN :inicio AND :fin " +
            "GROUP BY EXTRACT(MONTH FROM s.service_date) " +
            "ORDER BY EXTRACT(MONTH FROM s.service_date)",
            nativeQuery = true)
    List<Object[]> obtenerIngresosPorMes(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query(value = "SELECT s.service_date::date AS dia, SUM(s.workshop_share) AS total " +
            "FROM service_order s " +
            "WHERE s.service_date BETWEEN :inicio AND :fin " +
            "GROUP BY s.service_date::date " +
            "ORDER BY s.service_date::date",
            nativeQuery = true)
    List<Object[]> obtenerIngresosPorDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
