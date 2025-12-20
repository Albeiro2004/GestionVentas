package com.ventas.minipos.service;

import com.ventas.minipos.domain.*;
import com.ventas.minipos.dto.ListProductsDTO;
import com.ventas.minipos.exception.BusinessException;
import com.ventas.minipos.repo.InventoryRepository;
import com.ventas.minipos.repo.ProductRepository;
import com.ventas.minipos.repo.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final InventoryRepository inventoryRepository;

    public Product findProductById(String id) {
        return productRepository.findById(id)
                .orElse(null); // o lanzar excepción si no existe
    }

    public Page<ListProductsDTO> listProductsPage(Pageable pageable) {
        return  productRepository.findAllPage(pageable);
    }

    // Productos
    public List<ListProductsDTO> listProducts() {
        return productRepository.listProducts();
    }

    public Product updateProduct(Product product) {
        Product existing = findProductById(product.getId());
        existing.setNombre(product.getNombre());
        existing.setPrecioCompra(product.getPrecioCompra());
        existing.setPrecioVenta(product.getPrecioVenta());
        return productRepository.save(existing);
    }

    public void deleteProduct(String id) {
        Product existing = findProductById(id);

        long serviceCount = productRepository.countServiceProductsByProductId(existing.getId());
        if (serviceCount > 0) {
            throw new IllegalStateException("No se puede eliminar el producto. Está siendo usado en " + serviceCount + " servicios.");
        }

        long saleItemCount = productRepository.countSaleItemsByProductId(existing.getId());
        if (saleItemCount > 0) {
            throw new IllegalStateException("No se puede eliminar el producto. Está siendo usado en " + saleItemCount + " ventas.");
        }

        inventoryRepository.findByProductId(existing.getId())
                .ifPresent(inventoryRepository::delete);

        // ✅ Eliminar producto
        productRepository.delete(existing);
    }

    @Transactional
    public Purchase addPurchase(Purchase purchase) {
        if (purchase.getItems() == null) {
            purchase.setItems(new ArrayList<>());
        }

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItem item : purchase.getItems()) {
            // Validar integridad del ítem
            if (item.getTipo() == null) {
                throw new BusinessException("El tipo de ítem es obligatorio");
            }

            if (item.getTipo() == ItemType.PRODUCTO) {
                // Solo para productos: validar y actualizar inventario
                if (item.getProduct() == null || item.getProduct().getId() == null) {
                    throw new IllegalArgumentException("Producto es obligatorio para ítems de tipo PRODUCTO");
                }

                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProduct().getId()));

                item.setDescripcion("Compra de: " + product.getNombre());

                Optional<Inventory> optionalInventory = inventoryRepository.findByProduct(product);
                Inventory inventory = optionalInventory.orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setStock(0);
                    newInventory.setLocation("Zona no definida");
                    return newInventory;
                });

                inventory.setStock(inventory.getStock() + item.getCantidad());
                inventoryRepository.save(inventory); // Guardar el inventario, no el producto

                item.setProduct(product);
            } else {
                // Para HERRAMIENTA, SERVICIO, OTRO: no hay producto ni inventario
                if (item.getDescripcion() == null || item.getDescripcion().isBlank()) {
                    throw new IllegalArgumentException("Descripción es obligatoria para ítems de tipo " + item.getTipo());
                }

                item.setCantidad(1);

                item.setProduct(null); // Asegurar que no quede residuo
            }

            // Configurar relaciones y cálculos comunes
            item.setPurchase(purchase);
            item.calcularSubtotal();

            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }

        purchase.setTotal(total);
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> listPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }

}
