package com.ventas.minipos.dto;

import com.ventas.minipos.domain.Purchase;
import com.ventas.minipos.domain.PurchaseItem;
import com.ventas.minipos.domain.Product;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseRequest {

    private LocalDateTime date;
    private List <PurchaseItemRequest> items;

    public Purchase toPurchase() {
        Purchase purchase = new Purchase();
        purchase.setFecha(this.date);
        purchase.setItems(this.items.stream()
                .map(PurchaseItemRequest::toPurchaseItem)
                .collect(Collectors.toList()));
        return purchase;
    }

}
