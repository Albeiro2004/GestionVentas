package com.ventas.minipos.Auth;

import com.ventas.minipos.Jwt.JwtService;
import com.ventas.minipos.domain.Role;
import com.ventas.minipos.domain.User;
import com.ventas.minipos.repo.UserRepository;
import com.ventas.minipos.service.UserService;
import com.ventas.minipos.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    private HttpServletRequest req;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // Obtener la IP del cliente (si tienes HttpServletRequest disponible
        String ip = req.getRemoteAddr();

        // Registrar acceso
        userService.registrarAcceso(user, "LOGIN", ip);

        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())// <- aquí
                .name(user.getName())
                .username(user.getUsername())  // <- aquí
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // <- encriptar
                .name(request.getName())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return  AuthResponse.builder()
                .token(jwtService.getToken(user))
                .role(user.getRole().name())   // <- aquí
                .name(user.getName())
                .username(user.getUsername())  // <- aquí
                .build();
    }
}
