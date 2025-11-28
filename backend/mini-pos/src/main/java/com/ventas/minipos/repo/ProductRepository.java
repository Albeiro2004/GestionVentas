
package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.ListProductsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    // Buscar productos por nombre o ID
    @Query("select p.nombre, p.id, p.precioVenta, p.marca, p.precioCompra, i.stock, i.location FROM Product p, Inventory i where p.id = i.product.id " +
            "AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.id) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<ListProductsDTO> searchSuggestions(@Param("query") String query);

    @Query("SELECT COUNT(p) FROM Product p")
    Long countTotalProducts();

    // Productos con stock bajo
    @Query("SELECT COUNT(p) FROM Inventory p WHERE p.stock < 5 AND p.stock > 0")
    Long countLowStockProducts();

    // Productos sin stock
    @Query("SELECT COUNT(p) FROM Inventory p WHERE p.stock = 0")
    Long countOutOfStockProducts();

    // Valor total del inventario (stock * precioCompra)
    @Query("SELECT COALESCE(SUM(i.stock * p.precioCompra), 0) FROM Product p, Inventory i WHERE p.id = i.product.id")
    Double calculateInventoryValue();

    @Query("select p.nombre, p.id, p.precioVenta, p.marca, p.precioCompra, i.stock, i.location FROM Product p, Inventory i where p.id = i.product.id")
    List<ListProductsDTO> listProducts();

    @Query(value = "SELECT new com.ventas.minipos.dto.ListProductsDTO(p.nombre, p.id, p.precioVenta, p.marca, p.precioCompra, i.stock, i.location) " +
                    "FROM Product p JOIN Inventory i ON p.id = i.product.id",
            countQuery = "SELECT COUNT(p) FROM Product p JOIN Inventory i ON p.id = i.product.id"
    )
    Page<ListProductsDTO> findAllPage(Pageable pageable);

    @Query("SELECT COUNT(s) FROM ServiceProduct s WHERE s.product.id = :productId")
    long countServiceProductsByProductId(@Param("productId") String productId);

    @Query("SELECT COUNT(si) FROM SaleItem si WHERE si.product.id = :productId")
    long countSaleItemsByProductId(@Param("productId") String productId);

}