package com.ventas.minipos.service;

import com.ventas.minipos.domain.Product;
import com.ventas.minipos.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productoRepository;

    public List<Product> getSuggestions(String query) {
        return productoRepository.searchSuggestions(query);
    }
}
