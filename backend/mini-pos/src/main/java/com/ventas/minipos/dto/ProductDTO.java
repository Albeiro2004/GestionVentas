package com.ventas.minipos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    private String productId;
    private Integer quantity;
}