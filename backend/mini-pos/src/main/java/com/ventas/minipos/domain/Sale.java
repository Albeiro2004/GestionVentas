package com.ventas.minipos.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "documento", nullable = false)
    @JsonIgnoreProperties({"sales"})
    private Customer customer;

    @Enumerated(EnumType.STRING)

    private PaymentType paymentType;
    private double total;
    private LocalDateTime saleDate;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items;

    // Relación 1 a 1 con Deuda (si aplica)
    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Debt debt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
