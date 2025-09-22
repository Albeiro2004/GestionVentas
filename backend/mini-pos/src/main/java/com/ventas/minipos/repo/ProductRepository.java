
package com.ventas.minipos.repo;


import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.LowStockProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, String> {
    // Buscar productos por nombre o ID
    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.id) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchSuggestions(@Param("query") String query);

    // Total de productos
    @Query("SELECT COUNT(p) FROM Product p")
    Long countTotalProducts();

    // Productos con stock bajo
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < 5 AND p.stock > 0")
    Long countLowStockProducts();

    // Productos sin stock
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock = 0")
    Long countOutOfStockProducts();

    // Valor total del inventario (stock * precioCompra)
    @Query("SELECT SUM(p.stock * p.precioCompra) FROM Product p")
    Double calculateInventoryValue();

    @Query("SELECT new com.ventas.minipos.dto.LowStockProductDTO(p.nombre, p.stock, p.actualizadoEn) " +
            "FROM Product p " +
            "WHERE p.stock < :minStock")
    List<LowStockProductDTO> findLowStock(@Param("minStock") int minStock);


}