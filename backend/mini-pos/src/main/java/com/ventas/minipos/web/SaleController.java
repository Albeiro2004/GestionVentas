package com.ventas.minipos.web;

import com.ventas.minipos.dto.SaleRequest;
import com.ventas.minipos.dto.SaleRequest.Item;
import com.ventas.minipos.domain.*;
import com.ventas.minipos.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Ventas/sales")
public class SaleController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> registerSale(@RequestBody SaleRequest saleRequest) {
        Customer customer = customerRepository.findById(saleRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setPaymentType(saleRequest.getPaymentType());
        sale.setAbonoAmount(saleRequest.getAbonoAmount());
        sale.setSaleDate(LocalDateTime.now());

        // FIX: totalSale debe ser BigDecimal para mantener la precisión
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalSale = BigDecimal.ZERO;

        for (Item itemRequest : saleRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductId()));

            if (itemRequest.getCantidad() > product.getStock()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getNombre());
            }

            // Actualizar stock del producto
            product.setStock(product.getStock() - itemRequest.getCantidad());
            productRepository.save(product);

            // FIX: Realizar las operaciones con los métodos de BigDecimal
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
            // FIX: Sumar BigDecimal a BigDecimal
            totalSale = totalSale.add(subtotal);
        }

        sale.setItems(saleItems);
        // FIX: Asignar el BigDecimal a la entidad Sale
        sale.setTotal(totalSale.doubleValue());
        saleRepository.save(sale);

        return ResponseEntity.ok("Venta registrada exitosamente.");
    }
}