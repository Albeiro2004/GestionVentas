package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Sale;
import com.ventas.minipos.dto.SaleByDateDTO;
import com.ventas.minipos.dto.SaleDTO;
import com.ventas.minipos.dto.TopProductDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Cargar cliente, usuario, items y productos de una sola vez (evita N+1 queries)
    @EntityGraph(attributePaths = {"customer", "user"})
    List<Sale> findAll();

    @Query("select new com.ventas.minipos.dto.SaleDTO(" +
            "s.id, s.saleDate, s.total, new com.ventas.minipos.dto.CustomerDTO(c.documento, c.nombre), u.name) " +
            "from Sale s " +
            "join s.customer c " +
            "join s.user u")
    List<SaleDTO> findAllWithCustomerAndUser();

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Sale> findById(Long id);

    // ✅ Total ventas en el rango
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    Double findTotalSalesByDateRange(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    // ✅ Productos más vendidos en el rango
    @Query("""
        SELECT new com.ventas.minipos.dto.TopProductDTO(p.nombre, SUM(si.cantidad), p.precioCompra, p.precioVenta)
        FROM SaleItem si
        JOIN si.product p
        JOIN si.sale s
        WHERE s.saleDate BETWEEN :start AND :end
        GROUP BY p.nombre, p.precioCompra, p.precioVenta
        ORDER BY SUM(si.cantidad) DESC
    """)
    List<TopProductDTO> findTopSellingProducts(LocalDateTime start, LocalDateTime end);

    // ✅ Historial de ventas por día en el rango
    @Query("""
    SELECT new com.ventas.minipos.dto.SaleByDateDTO(s.saleDate, CAST(SUM(s.total) AS double))
    FROM Sale s
    WHERE s.saleDate BETWEEN :start AND :end
    GROUP BY s.saleDate
    ORDER BY s.saleDate ASC""")
    List<SaleByDateDTO> findSalesByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("select new com.ventas.minipos.dto.SaleDTO(" +
            "s.id, s.saleDate, s.total, new com.ventas.minipos.dto.CustomerDTO(c.documento, c.nombre), u.name) " +
            "from Sale s " +
            "join s.customer c " +
            "join s.user u " +
            "where s.saleDate between :start and :end")
    List<SaleDTO> findAllWithCustomerAndUserByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(f.total),0) FROM Sale f WHERE f.saleDate BETWEEN :start AND :end")
    Double sumByFechaBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Ventas agrupadas por mes
    @Query("SELECT MONTH(s.saleDate), COALESCE(SUM(s.total), 0) "+
           "FROM Sale s "+
            "WHERE s.saleDate BETWEEN :start AND :end "+
            "GROUP BY MONTH(s.saleDate) "+
            "ORDER BY MONTH(s.saleDate)")
    List<Object[]> sumVentasByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Ventas agrupadas por día (semana actual)
    @Query(value = "SELECT EXTRACT(DOW FROM f.sale_date) AS dow, COALESCE(SUM(f.total), 0) " +
            "FROM sale f " +
            "WHERE f.sale_date BETWEEN :start AND :end " +
            "GROUP BY EXTRACT(DOW FROM f.sale_date) " +
            "ORDER BY dow",
            nativeQuery = true)
    List<Object[]> sumVentasByDay(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);





}
