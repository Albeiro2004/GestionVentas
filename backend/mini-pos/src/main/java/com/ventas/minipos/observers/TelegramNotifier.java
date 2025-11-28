package com.ventas.minipos.observers;

import com.ventas.minipos.events.CreatedEvent;
import com.ventas.minipos.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TelegramNotifier {

    private final TelegramService telegramService;

    @EventListener
    public void onCreated(CreatedEvent event){
        String msg = "📦 Nuevo Registro de Producto:\n\n" +
                "🆔 ID: " + event.getProduct().getId() + "\n" +
                "🏷 Nombre: " + event.getProduct().getNombre() + "\n" +
                "🏭 Marca: " + event.getProduct().getMarca() + "\n" +
                "💲 Venta: " + event.getProduct().getPrecioVenta();

        telegramService.sendMessage(msg);
    }
}
