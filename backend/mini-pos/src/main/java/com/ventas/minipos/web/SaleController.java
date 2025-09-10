package com.ventas.minipos.web;

import com.ventas.minipos.dto.ErrorResponse;
import com.ventas.minipos.dto.SaleDTO;
import com.ventas.minipos.dto.SaleItemDTO;
import com.ventas.minipos.dto.SaleRequest;
import com.ventas.minipos.dto.SaleRequest.Item;
import com.ventas.minipos.domain.*;
import com.ventas.minipos.repo.*;
import com.ventas.minipos.service.SaleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Ventas/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository, invoiceRepository;

    @Autowired
    private DebtRepository debtRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PostMapping

    public ResponseEntity<?> registerSale(@RequestBody SaleRequest saleRequest) {
        try {
            // 🔐 Validar usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Usuario no autenticado"));
            }

            String username = authentication.getName();
            User usuario = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // 🔍 Validar cliente
            Customer customer = customerRepository.findById(saleRequest.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            String tipo = String.valueOf(saleRequest.getPaymentType());
            Double abono = saleRequest.getAbonoAmount() != null ? saleRequest.getAbonoAmount() : 0.0;

            // 🚫 Restricción: cliente genérico NO puede tener abono ni deuda
            if (isGenericCustomer(customer) && (tipo.equals("ABONO") || tipo.equals("DEBT"))) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "El cliente genérico no puede tener abonos ni deudas. Solo ventas de contado."
                ));
            }

            // 📝 Crear venta
            Sale sale = new Sale();
            sale.setCustomer(customer);
            sale.setPaymentType(saleRequest.getPaymentType());
            sale.setSaleDate(LocalDateTime.now());
            sale.setUser(usuario);

            List<SaleItem> saleItems = new ArrayList<>();
            BigDecimal totalSale = BigDecimal.ZERO;

            // 📦 Validar stock antes de descontar
            for (Item itemRequest : saleRequest.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemRequest.getProductId()));

                if (itemRequest.getCantidad() > product.getStock()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getNombre());
                }
            }

            // ✅ Descontar stock y crear ítems
            for (Item itemRequest : saleRequest.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemRequest.getProductId()));

                product.setStock(product.getStock() - itemRequest.getCantidad());
                productRepository.save(product);

                BigDecimal subtotal = itemRequest.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(itemRequest.getCantidad()))
                        .subtract(itemRequest.getDescuento());

                SaleItem saleItem = new SaleItem();
                saleItem.setProduct(product);
                saleItem.setSale(sale);
                saleItem.setCantidad(itemRequest.getCantidad());
                saleItem.setPrecioUnitario(itemRequest.getPrecioUnitario());
                saleItem.setDescuento(itemRequest.getDescuento());
                saleItem.setSubtotal(subtotal);

                saleItems.add(saleItem);
                totalSale = totalSale.add(subtotal);
            }

            sale.setItems(saleItems);
            sale.setTotal(totalSale.doubleValue());

            // 💾 Guardar venta primero
            saleRepository.save(sale);

            // 📌 Registrar deuda SOLO si corresponde
            if (!isGenericCustomer(customer) && ("ABONO".equals(tipo) || "DEBT".equals(tipo))) {
                double pendingAmount = totalSale.doubleValue() - abono;

                Debt debt = Debt.builder()
                        .sale(sale)
                        .totalAmount(totalSale.doubleValue())
                        .createAt(LocalDateTime.now())
                        .pendingAmount(pendingAmount)
                        .paid(pendingAmount <= 0)
                        .description(tipo.equals("ABONO")
                                ? "Venta a crédito. Abono inicial de " + abono
                                : "Venta a crédito. Sin abono inicial")
                        .build();

                // 💳 Si hubo abono, registrar el pago
                if (abono > 0) {
                    Payment payment = Payment.builder()
                            .debt(debt)
                            .amount(abono)
                            .paymentDate(LocalDateTime.now())
                            .build();

                    debt.getPayments().add(payment);
                }

                debtRepository.save(debt);
            }

            return ResponseEntity.ok(Map.of("message", "Venta registrada exitosamente."));

        } catch (IllegalArgumentException e) {
            // ⚠️ Errores de negocio (validación, stock, cliente, etc.)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 🛑 Cualquier otro error: rollback automático
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno en el servidor: " + e.getMessage()));
        }
    }

    // 🔎 Helper para identificar cliente genérico
    private boolean isGenericCustomer(Customer customer) {
        return "0000000000".equals(customer.getDocumento());
    }

    @GetMapping("/invoices")
    public List<SaleDTO> getSales() {
        return saleService.getAllSales();
    }

    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        try {
            saleService.deleteSale(id);
            return ResponseEntity.ok("Factura eliminada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/invoices/latest")
    public List<SaleDTO> getLatestInvoices() {
        return saleService.getAllSales().stream()
                .sorted(Comparator.comparing(SaleDTO::getSaleDate).reversed())
                .limit(20)
                .toList();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<SaleItemDTO>> getSaleItems(@PathVariable Long id) {
        List<SaleItemDTO> items = saleService.getSaleItems(id);
        return ResponseEntity.ok(items);
    }


}
