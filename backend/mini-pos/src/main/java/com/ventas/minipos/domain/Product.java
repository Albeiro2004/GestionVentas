package com.ventas.minipos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @Column(length = 40)
    private String id;

    @NotBlank
    private String nombre;

    @Min(0)
    private BigDecimal precioCompra;

    @Min(0)
    private BigDecimal precioVenta;

    private String marca;

    private Instant creadoEn;

    private Instant actualizadoEn;

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }
}
