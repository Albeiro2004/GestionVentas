package com.ventas.minipos.service;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.domain.PurchaseItem;
import com.ventas.minipos.domain.Sale;
import com.ventas.minipos.domain.SaleItem;
import com.ventas.minipos.dto.PurchaseRequest;
import com.ventas.minipos.repo.ProductRepository;
import com.ventas.minipos.repo.PurchaseRepository;
import com.ventas.minipos.repo.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleRepository saleRepository;

    public InventoryService(ProductRepository productRepository,
                            PurchaseRepository purchaseRepository,
                            SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.saleRepository = saleRepository;
    }
    public Product findProductById(String id) {
        return productRepository.findById(id)
                .orElse(null); // o lanzar excepción si no existe
    }

    // Productos
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product existing = findProductById(product.getId());
        existing.setNombre(product.getNombre());
        existing.setPrecioCompra(product.getPrecioCompra());
        existing.setPrecioVenta(product.getPrecioVenta());
        existing.setStock(product.getStock());
        return productRepository.save(existing);
    }

    public void deleteProduct(String id) {
        Product existing = findProductById(id);
        productRepository.delete(existing);
    }

    // Compras
    @Transactional
    public Purchase addPurchase(Purchase purchase) {
        if (purchase.getItems() == null) {
            purchase.setItems(new ArrayList<>());
        }

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItem item : purchase.getItems()) {
            // Traer el producto desde DB
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProduct().getId()));

            // Actualizar stock
            product.setStock(product.getStock() + item.getCantidad());
            productRepository.save(product);

            // Asignar producto al item y calcular subtotal
            item.setProduct(product);
            item.setPurchase(purchase);
            item.calcularSubtotal();

            // Sumar al total de la compra
            total = total.add(item.getSubtotal());
        }

        // Asignar total a la compra
        purchase.setTotal(total);

        // Guardar la compra completa
        return purchaseRepository.save(purchase);
    }


    // Ventas
    @Transactional
    public Sale addSale(Sale sale) {
        for (SaleItem item : sale.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow();
            if (product.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para producto: " + product.getNombre());
            }
            product.setStock(product.getStock() - item.getCantidad());
            productRepository.save(product);
        }
        return saleRepository.save(sale);
    }
    public List<Purchase> listPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }

}
