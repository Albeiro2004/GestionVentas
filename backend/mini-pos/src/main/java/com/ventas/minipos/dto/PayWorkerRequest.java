package com.ventas.minipos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayWorkerRequest {
    private Long workerId;
    private double amount;
}
