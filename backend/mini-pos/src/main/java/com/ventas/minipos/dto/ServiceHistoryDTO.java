package com.ventas.minipos.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ServiceHistoryDTO {
    private Long id;
    private LocalDate serviceDate;
    private String customerName;
    private String description;
    private Double total;
    private Double workerShare;
    private Double workshopShare;
    private List<ProductServiceDTO> products;
}