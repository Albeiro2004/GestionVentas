package com.ventas.minipos.web;

import com.ventas.minipos.domain.ServiceOrder;
import com.ventas.minipos.dto.ServiceHistoryDTO;
import com.ventas.minipos.dto.ServiceOrderRequest;
import com.ventas.minipos.service.ServiceOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Ventas/services")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping("/register")
    public ResponseEntity<ServiceOrder> register(@Valid @RequestBody ServiceOrderRequest request) {
        ServiceOrder order = serviceOrderService.registerService(request);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/history")
    public ResponseEntity<List<ServiceHistoryDTO>> getServicesHistory(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) String customerId) {

        List<ServiceHistoryDTO> result = serviceOrderService.getFilteredServices(
                startDate, endDate, workerId, customerId
        );

        return ResponseEntity.ok(result);
    }

}
