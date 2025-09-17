package com.ventas.minipos.repo;


import com.ventas.minipos.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // ✅ Total compras en el rango
    @Query("SELECT SUM(p.total) FROM Purchase p WHERE p.fecha BETWEEN :start AND :end")
    Double findTotalPurchasesByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(e.total),0) FROM Purchase e")
    Double sumTotalEgresos();

}