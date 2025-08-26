package com.ventas.minipos.dto;

import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.domain.PurchaseItem;
import com.ventas.minipos.domain.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseRequest {

    private List<Item> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        private String productId;
        private Integer cantidad;
        private Double costoUnitario;
    }

    // Convierte el DTO a la entidad Purchase
    public Purchase toEntity() {
        Purchase purchase = new Purchase();
        if (items != null) {
            purchase.setItems(new ArrayList<>());
            for (Item dtoItem : items) {
                PurchaseItem item = new PurchaseItem();
                Product product = new Product();
                product.setId(dtoItem.getProductId());
                item.setProduct(product);
                item.setCantidad(dtoItem.getCantidad());
                item.setCostoUnitario(dtoItem.getCostoUnitario() != null
                        ? BigDecimal.valueOf(dtoItem.getCostoUnitario())
                        : BigDecimal.ZERO);
                purchase.getItems().add(item);
            }
        }
        return purchase;
    }

}
