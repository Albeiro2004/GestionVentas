package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}

