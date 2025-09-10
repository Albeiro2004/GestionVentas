package com.ventas.minipos.repo;

import com.ventas.minipos.domain.SaleItem;
import com.ventas.minipos.dto.SaleItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    @Query("SELECT new com.ventas.minipos.dto.SaleItemDTO(" +
            "i.id, i.cantidad, i.precioUnitario, i.descuento,i.subtotal, p.nombre) " +
            "FROM SaleItem i " +
            "JOIN i.product p " +
            "WHERE i.sale.id = :saleId")
    List<SaleItemDTO> findItemsBySaleId(@Param("saleId") Long saleId);

}
