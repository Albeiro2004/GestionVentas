package com.ventas.minipos.web;

import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.dto.PurchaseRequest;
import com.ventas.minipos.service.InventoryService;
import com.ventas.minipos.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Ventas/purchases")
public class PurchaseController {

    private final InventoryService inventoryService;
    private final PurchaseService purchaseService;

    public PurchaseController(InventoryService inventoryService, PurchaseService purchaseService) {
        this.inventoryService = inventoryService;
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        Purchase savedPurchase = inventoryService.addPurchase(request.toPurchase());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPurchase);
    }

    // Listar todas las compras (historial)
    @GetMapping
    public List<Purchase> listPurchases() {
        return inventoryService.listPurchases();
    }

    // Opcional: eliminar compra
    @DeleteMapping("/{id}")
    public void deletePurchase(@PathVariable Long id) {
        inventoryService.deletePurchase(id);
    }
}
