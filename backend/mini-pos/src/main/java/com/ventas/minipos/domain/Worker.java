package com.ventas.minipos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    private Long id;

    private Long documento;
    private String name;
    private String specialty;
    private Double commission;
}
