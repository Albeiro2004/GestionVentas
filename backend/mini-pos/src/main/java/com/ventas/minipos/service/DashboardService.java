// src/main/java/com/ventas/minipos/service/DashboardService.java
package com.ventas.minipos.service;

import com.ventas.minipos.dto.DashboardStatsDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DashboardService {

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setVentas(150);
        stats.setCompras(80);
        stats.setProductos(45);
        stats.setReportes(12);

        stats.setHistorialVentas(Arrays.asList(10, 25, 50, 75, 150));
        stats.setHistorialCompras(Arrays.asList(5, 20, 40, 60, 80));
        stats.setHistorialProductos(Arrays.asList(10, 20, 30, 40, 45));
        stats.setHistorialReportes(Arrays.asList(1, 3, 5, 8, 12));

        return stats;
    }
}
