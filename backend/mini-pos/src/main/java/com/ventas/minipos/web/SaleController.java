package com.ventas.minipos.web;

import com.ventas.minipos.dto.SaleRequest;
import com.ventas.minipos.dto.SaleRequest.Item;
import com.ventas.minipos.domain.*;
import com.ventas.minipos.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/Ventas/sales")
public class SaleController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private DebtRepository debtRepository;

    @Transactional
    @PostMapping
    public ResponseEntity<?> registerSale(@RequestBody SaleRequest saleRequest) {
        try {
            Customer customer = customerRepository.findById(saleRequest.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            // 🔒 Restricción: cliente genérico NO puede tener abono ni deuda
            String tipo = String.valueOf(saleRequest.getPaymentType());
            Double abono = saleRequest.getAbonoAmount();
            if (isGenericCustomer(customer) && (tipo.equals("ABONO") || tipo.equals("DEBT"))) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "El cliente genérico no puede tener abonos ni deudas. Solo ventas de contado."
                ));
            }

            Sale sale = new Sale();
            sale.setCustomer(customer);
            sale.setPaymentType(saleRequest.getPaymentType());
            sale.setSaleDate(LocalDateTime.now());

            List<SaleItem> saleItems = new ArrayList<>();
            BigDecimal totalSale = BigDecimal.ZERO;

            // Calcular ítems y actualizar stock
            for (Item itemRequest : saleRequest.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemRequest.getProductId()));

                if (itemRequest.getCantidad() > product.getStock()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getNombre());
                }

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
            saleRepository.save(sale);

            // Registrar deuda si hay abono y NO es cliente genérico
            if (!isGenericCustomer(customer)) {
                Debt debt = Debt.builder()
                        .sale(sale)
                        .totalAmount(totalSale.doubleValue())
                        .createAt(LocalDateTime.now())
                        .pendingAmount(totalSale.doubleValue() - abono)
                        .paid(false)
                        .description("Venta a crédito. Abono inicial de " + abono)
                        .build();

                Payment payment = Payment.builder()
                        .debt(debt)
                        .amount(abono)
                        .paymentDate(LocalDateTime.now())
                        .build();

                debt.getPayments().add(payment);
                debtRepository.save(debt);
            }

            return ResponseEntity.ok(Map.of("message", "Venta registrada exitosamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno en el servidor"));
        }
    }

    // 🔎 Helper para identificar cliente genérico
    private boolean isGenericCustomer(Customer customer) {
        return "0000000000".equals(customer.getDocumento());
    }

}
