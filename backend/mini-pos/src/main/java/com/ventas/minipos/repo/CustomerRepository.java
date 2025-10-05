package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<com.ventas.minipos.domain.Customer, String> {
    Optional<Customer> findByDocumentoOrNombreContainingIgnoreCase(String documento, String nombre);
    // Buscar clientes por nombre o documento
    @Query("SELECT c FROM Customer c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.documento) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> searchSuggestions(@Param("query") String query);

    Optional<Customer> findByDocumento(String documento);

}