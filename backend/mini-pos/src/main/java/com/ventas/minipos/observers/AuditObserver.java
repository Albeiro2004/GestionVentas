package com.ventas.minipos.observers;

import com.ventas.minipos.events.CreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditObserver {

    private static final Logger log = LoggerFactory.getLogger(AuditObserver.class);

    @EventListener
    public void onCreated(CreatedEvent event){
        log.info("Producto creado: {} (Marca: {})",
            event.getProduct().getId(),
            event.getProduct().getMarca()
        );
    }
}
