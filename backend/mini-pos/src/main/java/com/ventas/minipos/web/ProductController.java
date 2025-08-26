package com.ventas.minipos.web;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.service.InventoryService;
import com.ventas.minipos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/Ventas/products")
public class ProductController {

    @Autowired
    private ProductService productoService;
    private final InventoryService inventoryService;

    public ProductController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return inventoryService.listProducts();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return inventoryService.saveProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        // Aseguramos que el ID del path y el del objeto sean iguales
        if (!id.equals(product.getId())) {
            throw new IllegalArgumentException("El ID del producto no coincide");
        }
        return inventoryService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        inventoryService.deleteProduct(id);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<Product>> getProductSuggestions(@RequestParam String query) {
        if (query.length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(productoService.getSuggestions(query));
    }
}

