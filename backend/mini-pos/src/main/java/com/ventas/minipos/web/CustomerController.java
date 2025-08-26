package com.ventas.minipos.web;

import com.ventas.minipos.domain.Customer;
import com.ventas.minipos.repo.CustomerRepository;
import com.ventas.minipos.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/Ventas/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    private final CustomerService customerService;

    // 👇 Spring inyecta automáticamente el servicio
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> searchCustomer(@RequestParam String query) {
        return customerRepository.findByDocumentoOrNombreContainingIgnoreCase(query, query)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<Customer>> getCustomerSuggestions(@RequestParam String query) {
        if (query.length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(customerService.getSuggestions(query));
    }

    @GetMapping("/generic")
    public ResponseEntity<Customer> getGenericCustomer() {
        return ResponseEntity.ok(customerService.getGenericCustomer());
    }


}
