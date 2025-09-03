package com.ventas.minipos.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount; // Monto del abono
    private LocalDateTime paymentDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;
}
