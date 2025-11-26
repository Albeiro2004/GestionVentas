package com.ventas.minipos.service;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.dto.ListProductsDTO;
import com.ventas.minipos.dto.ProductCreateRequest;
import com.ventas.minipos.factory.ProductFactory;
import com.ventas.minipos.factory.ProductFactoryMethod;
import com.ventas.minipos.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productoRepository;

    private  final ProductFactoryMethod factory = new ProductFactory();

    public List<ListProductsDTO> getSuggestions(String query) {
        return productoRepository.searchSuggestions(query);
    }

    public Product create(ProductCreateRequest dto) {
        Product product = factory.createProduct(dto);
        return productoRepository.save(product);
    }

}
