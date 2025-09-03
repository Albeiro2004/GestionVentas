package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    // Trae las deudas con sus pagos, la venta y el cliente para evitar lazy init
    @Query("SELECT DISTINCT d FROM Debt d " +
            "LEFT JOIN FETCH d.payments p " +
            "LEFT JOIN FETCH d.sale s " +
            "LEFT JOIN FETCH s.customer c")
    List<Debt> findAllWithPaymentsAndSaleAndCustomer();

    List<Debt> findBySale_Customer_Documento(String documento);

    Long countByPaidFalse();

}

