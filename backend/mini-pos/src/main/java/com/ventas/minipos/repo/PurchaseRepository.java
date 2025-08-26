package com.ventas.minipos.repo;


import com.ventas.minipos.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {}