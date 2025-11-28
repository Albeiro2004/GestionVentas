package com.ventas.minipos.events;

import com.ventas.minipos.domain.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreatedEvent extends ApplicationEvent {

    private final Product product;

    public CreatedEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }

}
