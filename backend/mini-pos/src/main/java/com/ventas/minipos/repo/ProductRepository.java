
package com.ventas.minipos.repo;


import com.ventas.minipos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, String> {
    // Buscar productos por nombre o ID
    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.id) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchSuggestions(@Param("query") String query);
}