package com.ventas.minipos.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Purchase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant fecha;
    private BigDecimal total;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PurchaseItem> items = new ArrayList<>();


    @PrePersist void pre(){ if (fecha==null) fecha=Instant.now(); }

    public void calcularTotal() {
        if (items != null) {
            total = items.stream()
                    .map(PurchaseItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}