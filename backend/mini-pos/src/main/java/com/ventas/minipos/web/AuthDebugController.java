package com.ventas.minipos.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class AuthDebugController {

    @GetMapping("/auth/debug")
    public String debugRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return "No hay autenticación activa";
        }

        String username = auth.getName();
        String roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(", "));

        return "Usuario: " + username + " | Roles: " + roles;
    }
}