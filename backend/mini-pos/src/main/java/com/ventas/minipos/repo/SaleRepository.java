package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Sale;
import com.ventas.minipos.dto.SaleDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Cargar cliente, usuario, items y productos de una sola vez (evita N+1 queries)
    @EntityGraph(attributePaths = {"customer", "user"})
    List<Sale> findAll();

    @Query("select new com.ventas.minipos.dto.SaleDTO(" +
            "s.id, s.saleDate, s.total, new com.ventas.minipos.dto.CustomerDTO(c.nombre, c.documento), u.name) " +
            "from Sale s " +
            "join s.customer c " +
            "join s.user u")
    List<SaleDTO> findAllWithCustomerAndUser();


    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Sale> findById(Long id);

}
