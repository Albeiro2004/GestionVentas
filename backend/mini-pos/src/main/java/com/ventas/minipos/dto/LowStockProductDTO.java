package com.ventas.minipos.dto;

import java.time.Instant;

public record LowStockProductDTO(
        String nombre,
        Integer stock,
        Instant actualizadoEn
) {}
