package com.ventas.minipos.service;

import com.ventas.minipos.domain.Customer;
import com.ventas.minipos.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository clienteRepository;

    public List<Customer> getSuggestions(String query) {
        return clienteRepository.searchSuggestions(query);
    }
    public Customer getGenericCustomer() {
        return clienteRepository.findByDocumento("0000000000") // 👈 usa un doc fijo
                .orElseThrow(() -> new RuntimeException("Cliente genérico no existe en BD"));
    }

}
