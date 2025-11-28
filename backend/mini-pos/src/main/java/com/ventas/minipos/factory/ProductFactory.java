package com.ventas.minipos.factory;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.ProductCreateRequest;

import java.time.Instant;
import java.util.UUID;

public class ProductFactory implements ProductFactoryMethod {

    @Override
    public Product createProduct(ProductCreateRequest request) {
        return Product.builder()
                .id(request.getId() != null ? request.getId() : UUID.randomUUID().toString())
                .nombre(request.getNombre())
                .precioCompra(request.getPrecioCompra())
                .precioVenta(request.getPrecioVenta())
                .marca(request.getMarca())
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();
    }
}
