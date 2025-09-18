// src/main/java/com/tuproyecto/controller/DashboardController.java

package com.ventas.minipos.web;

import com.ventas.minipos.repo.CustomerRepository;
import com.ventas.minipos.repo.ProductRepository;
import com.ventas.minipos.repo.PurchaseRepository;
import com.ventas.minipos.repo.SaleRepository;
import com.ventas.minipos.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Ventas")
@CrossOrigin(origins = "")
public class DashboardController {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SaleService saleService;

    public DashboardController(SaleRepository saleRepository, PurchaseRepository purchaseRepository, CustomerRepository customerRepository, ProductRepository productRepository, SaleService saleService) {
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.saleService = saleService;
    }

    // Ventas por periodo
    @GetMapping("/dashboard/sales")
    public Map<String, Object> getSalesSummary(@RequestParam String period) {
        Double totalActual = saleService.sumVentasByPeriod(period);
        Double totalAnterior = saleService.sumVentasByPreviousPeriod(period);

        Double cambio = 0.0;
        if (totalAnterior != null && totalAnterior > 0) {
            cambio = ((totalActual - totalAnterior) / totalAnterior) * 100;
        }

        return Map.of("total_sales", totalActual, "change", cambio
        );
    }

    // Egresos
    @GetMapping("/dashboard/expenses")
    public Map<String, Object> getExpensesSummary() {
        Double total = purchaseRepository.sumTotalEgresos();
        return Map.of("total_expenses", total, "change", 0);
    }

    // Productos
    @GetMapping("/dashboard/products")
    public Map<String, Object> getProductsSummary() {
        Long total = productRepository.count();
        return Map.of("total_products", total, "change", 0);
    }

    // Clientes
    @GetMapping("/dashboard/customers")
    public Map<String, Object> getCustomersSummary() {
        Long total = customerRepository.count()-1;
        return Map.of("total_customers", total, "change", 0);
    }

}