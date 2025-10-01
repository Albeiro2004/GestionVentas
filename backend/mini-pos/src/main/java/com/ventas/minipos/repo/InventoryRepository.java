package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Inventory;
import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.LowStockProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    @Query("SELECT new com.ventas.minipos.dto.LowStockProductDTO(p.nombre, i.stock, p.actualizadoEn) " +
            "FROM Inventory i JOIN i.product p " +
            "WHERE i.stock < :minStock " +
            "ORDER BY i.stock ASC " +  // Opcional: ordenar por stock más bajo primero
            "LIMIT 5")
    List<LowStockProductDTO> findLowStock(@Param("minStock") int minStock);

    Optional<Inventory> findByProductId(String productId);
}
