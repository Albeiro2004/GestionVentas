package com.ventas.minipos.repo;


import com.ventas.minipos.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SaleRepository extends JpaRepository<Sale, Long> {}