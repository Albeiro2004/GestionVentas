// src/main/java/com/tuproyecto/controller/DashboardController.java

package com.ventas.minipos.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/Ventas")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestParam String type) {
        switch (type.toLowerCase()) {
            case "products":
                return ResponseEntity.ok(getProductSummary());
            case "sales":
                return ResponseEntity.ok(getSalesSummary());
            case "purchases":
                return ResponseEntity.ok(getPurchasesSummary());
            default:
                return ResponseEntity.badRequest().body("Tipo de resumen no válido: " + type);
        }
    }

    private Map<String, Object> getProductSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("total_products", 345);
        data.put("low_stock_count", 15);
        data.put("most_popular_product", "Bicicleta de Montaña");
        data.put("products_by_category", Map.of("Bicicletas", 150, "Accesorios", 120, "Repuestos", 75));
        return data;
    }

    private Map<String, Object> getSalesSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("total_sales", 150000.50);
        data.put("sales_count", 250);
        data.put("average_sale_price", 600.00);
        data.put("monthly_sales", Map.of("Ene", 15000.0, "Feb", 22000.0, "Mar", 25000.0, "Abr", 30000.0, "May", 28000.0, "Jun", 35000.50));
        return data;
    }

    private Map<String, Object> getPurchasesSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("total_purchases", 95000.75);
        data.put("purchase_count", 120);
        data.put("most_common_supplier", "Proveedor A");
        data.put("purchase_by_category", Map.of("Bicicletas", 60000.0, "Accesorios", 20000.0, "Repuestos", 15000.75));
        return data;
    }
}