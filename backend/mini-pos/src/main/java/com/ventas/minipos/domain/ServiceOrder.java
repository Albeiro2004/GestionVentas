package com.ventas.minipos.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Double laborCost;
    private Double totalPrice;
    private Double workerShare;
    private Double workshopShare;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    private LocalDateTime serviceDate;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceProduct> products = new ArrayList<>();

    public void addproduct(Product product, Integer quantity) {
        ServiceProduct sp = new ServiceProduct();
        sp.setProduct(product);
        sp.setQuantity(quantity);
        sp.setServiceOrder(this);
        this.products.add(sp);
    }

    public void calculateShares(){
        double totalProducts = this.products.stream()
                .mapToDouble(sp -> sp.getProduct().getPrecioVenta().doubleValue() * sp.getQuantity())
                .sum();

        this.totalPrice = totalProducts + this.laborCost;

        if (this.worker != null && this.worker.getCommission() != null) {
            this.workerShare = this.laborCost * this.worker.getCommission();
        } else {
            this.workerShare = 0.0;
        }

        this.workshopShare = this.laborCost - this.workerShare;
    }
}