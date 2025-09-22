// src/main/java/com/tuproyecto/controller/DashboardController.java

package com.ventas.minipos.web;

import com.ventas.minipos.dto.SaleResponse;
import com.ventas.minipos.repo.*;
import com.ventas.minipos.service.PurchaseService;
import com.ventas.minipos.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final PurchaseService purchaseService;

    public DashboardController(SaleRepository saleRepository, PurchaseRepository purchaseRepository, CustomerRepository customerRepository, ProductRepository productRepository, SaleService saleService, PurchaseService purchaseService) {
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.saleService = saleService;
        this.purchaseService = purchaseService;
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
    public Map<String, Object> getExpensesSummary(@RequestParam String period) {
        Double totalActual = purchaseService.sumVentasByPeriod(period);
        Double totalAnterior = purchaseService.sumVentasByPreviousPeriod(period);

        Double cambio = 0.0;
        if (totalAnterior != null && totalAnterior > 0) {
            cambio = ((totalActual - totalAnterior) / totalAnterior) * 100;
        }
        return Map.of("total_expenses", totalActual, "change", cambio);
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

    @GetMapping("/dashboard/chart")
    public SaleResponse getSales(@RequestParam(defaultValue = "week") String period) {
        return saleService.getSalesByPeriod(period);
    }

    @GetMapping("/dashboard/topProducts")
    public List<TopProductResponse> getTopProducts() {
        return saleService.getTopProducts();
    }


}