
package com.ventas.minipos.web;

import com.ventas.minipos.dto.SaleResponse;
import com.ventas.minipos.dto.SystemAlertResponse;
import com.ventas.minipos.dto.TopProductResponse;
import com.ventas.minipos.repo.*;
import com.ventas.minipos.service.DashboardService;
import com.ventas.minipos.service.PurchaseService;
import com.ventas.minipos.service.SaleService;
import com.ventas.minipos.service.SystemAlertService;
import org.springframework.http.ResponseEntity;
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
    private final SystemAlertService systemAlertService;
    private DashboardService dashboardService;

    public DashboardController(SaleRepository saleRepository, PurchaseRepository purchaseRepository, CustomerRepository customerRepository, ProductRepository productRepository, SaleService saleService, PurchaseService purchaseService, SystemAlertService systemAlertService, DashboardService dashboardService) {
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.systemAlertService = systemAlertService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/services")
    public ResponseEntity<SaleResponse> obtenerReporteIngresosServicios(@RequestParam(defaultValue = "year") String period) {

        SaleResponse data = dashboardService.obtenerIngresosServicios(period);
        return ResponseEntity.ok(data);
    }

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

    @GetMapping("/dashboard/products")
    public Map<String, Object> getProductsSummary() {
        Long total = productRepository.count();
        return Map.of("total_products", total, "change", 0);
    }

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

    @GetMapping("/dashboard/alerts")
    public List<SystemAlertResponse> getAlerts() {
        return systemAlertService.getSystemAlerts();
    }


}