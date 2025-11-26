package com.ventas.minipos.factory;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.ProductCreateRequest;

public interface ProductFactoryMethod {
    Product createProduct(ProductCreateRequest request);
}
