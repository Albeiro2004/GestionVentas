package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByDocumento(Long id);
}
