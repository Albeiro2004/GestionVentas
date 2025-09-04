
package com.ventas.minipos.dto;

import java.time.LocalDateTime;

public record AccessDTO(
        Long id,
        LocalDateTime fecha,
        String ip,
        String estado
) {}
