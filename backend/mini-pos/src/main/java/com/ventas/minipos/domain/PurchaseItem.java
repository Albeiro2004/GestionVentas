package com.ventas.minipos.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;


@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @JsonBackReference
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",  nullable = true)
    private Product product;

    @Enumerated(EnumType.STRING)
    private ItemType tipo;

    private String descripcion;

    private Integer cantidad;
    private BigDecimal costoUnitario; // costo real de esta compra
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null && costoUnitario != null) {
            subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}