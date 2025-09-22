package com.ventas.minipos.repo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductResponse {
    private String id;
    private String name;
    private int sales;
    private Double revenue;
}
