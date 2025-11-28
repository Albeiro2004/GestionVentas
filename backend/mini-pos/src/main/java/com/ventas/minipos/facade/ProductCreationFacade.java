package com.ventas.minipos.facade;

import com.ventas.minipos.domain.Inventory;
import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.ProductCreateRequest;
import com.ventas.minipos.events.CreatedEvent;
import com.ventas.minipos.factory.ProductFactory;
import com.ventas.minipos.factory.ProductFactoryMethod;
import com.ventas.minipos.repo.InventoryRepository;
import com.ventas.minipos.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCreationFacade {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Product createProductWithInventory(ProductCreateRequest request) {

        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser un número >= 0");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación es obligatoria");
        }

        ProductFactoryMethod factory = new ProductFactory();
        Product product = factory.createProduct(request);

        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setLocation(request.getLocation().trim());
        inventory.setStock(request.getStock());
        inventoryRepository.save(inventory);

        eventPublisher.publishEvent(new CreatedEvent(this, savedProduct));

        return savedProduct;
    }
}



