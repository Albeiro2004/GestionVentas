package com.ventas.minipos.service;

import com.ventas.minipos.dto.LowStockProductDTO;
import com.ventas.minipos.dto.SystemAlertResponse;
import com.ventas.minipos.repo.InventoryRepository;
import com.ventas.minipos.repo.ProductRepository;
import com.ventas.minipos.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemAlertService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public SystemAlertService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<SystemAlertResponse> getSystemAlerts() {
        List<SystemAlertResponse> alerts = new ArrayList<>();

        // 1. Stock bajo
        List<LowStockProductDTO> lowStockProducts = inventoryRepository.findLowStock(2);
        for (LowStockProductDTO product : lowStockProducts) {
            alerts.add(new SystemAlertResponse(
                    "Stock Bajo",
                    product.nombre() + " tiene " + product.stock() + " unidades en inventario",
                    TimeUtils.humanReadableTime(product.actualizadoEn()),
                    "fas fa-exclamation-triangle",
                    "text-warning",
                    "alert-warning-soft"
            ));
        }

        /*
        // 2. Meta de ventas alcanzada (ejemplo simplificado)
        boolean salesGoalAchieved = true; // aquí iría tu lógica real
        if (salesGoalAchieved) {
            alerts.add(new SystemAlertResponse(
                    "Meta Alcanzada",
                    "Has superado la meta de ventas del mes en un 12%",
                    TimeUtils.humanReadableTime(LocalDateTime.now().minusHours(1)),
                    "fas fa-trophy",
                    "text-success",
                    "alert-success-soft"
            ));
        }

        // 3. Pedido pendiente (ejemplo dummy)
        alerts.add(new SystemAlertResponse(
                "Nuevo Pedido",
                "Pedido pendiente de Samsung Galaxy S23 por 10 unidades",
                TimeUtils.humanReadableTime(LocalDateTime.now().minusHours(2)),
                "fas fa-bell",
                "text-primary",
                "alert-primary-soft"
        ));*/

        return alerts;
    }
}