package com.ventas.minipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemAlertResponse {
    private String title;
    private String message;
    private String time;
    private String icon;
    private String iconClass;
    private String alertClass;
}
