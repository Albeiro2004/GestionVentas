package com.ventas.minipos.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @JsonIgnoreProperties({"debt"})
    private Sale sale;

    private Double totalAmount;

    private Double pendingAmount;

    private LocalDateTime createAt = LocalDateTime.now();

    private Boolean paid;

    private String description;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default  // para evitar NullPointer con builder
    private List<Payment> payments = new ArrayList<>();

    public void addPayment(Payment payment) {
        // Relacionar el pago con la deuda
        payment.setDebt(this);

        // Registrar en la lista
        this.payments.add(payment);

        // Restar del monto pendiente
        if (this.pendingAmount == null) {
            this.pendingAmount = this.totalAmount;
        }

        this.pendingAmount -= payment.getAmount();

        // Evitar valores negativos
        if (this.pendingAmount < 0) {
            this.pendingAmount = 0.0;
        }

        // Marcar como pagada si ya está saldada
        this.paid = this.pendingAmount == 0.0;
    }

}
