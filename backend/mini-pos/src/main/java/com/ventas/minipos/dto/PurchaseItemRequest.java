package com.ventas.minipos.dto;

import com.ventas.minipos.domain.ItemType;
import com.ventas.minipos.domain.Product;
import com.ventas.minipos.domain.PurchaseItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemRequest {

    @NotNull
    private ItemType tipo;

    @NotNull
    private Integer cantidad;

    @NotNull
    private BigDecimal costoUnitario;

    private String descripcion;

    private String productId;

    public PurchaseItem toPurchaseItem() {
        PurchaseItem item = new PurchaseItem();
        item.setTipo(this.tipo);
        item.setCantidad(this.cantidad);
        item.setCostoUnitario(this.costoUnitario);
        item.setDescripcion(this.descripcion);

        if (this.productId != null) {
             Product product = new Product();
             product.setId(this.productId);
             item.setProduct(product);
        }
        return item;
    }
}
