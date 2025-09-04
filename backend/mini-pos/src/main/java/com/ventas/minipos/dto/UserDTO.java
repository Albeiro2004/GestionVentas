package com.ventas.minipos.dto;

public record UserDTO(
        Long id,
        String username,
        String name,
        String role // cambiar Role a String
) {}
