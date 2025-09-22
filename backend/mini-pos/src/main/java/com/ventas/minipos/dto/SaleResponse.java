package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SaleResponse {
    private List<String> labels;
    private List<Double> actual;
    private List<Double> previous;
}
