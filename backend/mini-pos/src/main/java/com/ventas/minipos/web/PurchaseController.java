package com.ventas.minipos.web;

import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.dto.PurchaseRequest;
import com.ventas.minipos.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Ventas/purchases")
public class PurchaseController {

    private final InventoryService inventoryService;

    public PurchaseController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Crear compra
    @PostMapping
    public Purchase addPurchase(@RequestBody PurchaseRequest request) {
        return inventoryService.addPurchase(request.toEntity());
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
