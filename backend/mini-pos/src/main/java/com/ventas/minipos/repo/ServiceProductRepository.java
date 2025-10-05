package com.ventas.minipos.repo;

import com.ventas.minipos.domain.ServiceOrder;
import com.ventas.minipos.domain.ServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long> {
    List<ServiceProduct> findByServiceOrder(ServiceOrder serviceOrder);
}
