package com.ventas.minipos.repo;

import com.ventas.minipos.domain.Access;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessRepository extends JpaRepository<Access, Long> {
    List<Access> findByUser_Id(Long userId);

}
