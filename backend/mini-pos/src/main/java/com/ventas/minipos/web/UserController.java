package com.ventas.minipos.web;

import com.ventas.minipos.Auth.AuthResponse;
import com.ventas.minipos.Auth.AuthService;
import com.ventas.minipos.Auth.RegisterRequest;
import com.ventas.minipos.Jwt.JwtService;
import com.ventas.minipos.domain.User;
import com.ventas.minipos.dto.AccessDTO;
import com.ventas.minipos.dto.UserDTO;
import com.ventas.minipos.repo.UserRepository;
import com.ventas.minipos.service.UserService;
import com.ventas.minipos.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Ventas/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService usuarioService;
    private final UserService userService;
    private final UserServiceImpl userServiceImpl;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping
    public ResponseEntity<UserDTO> crearUsuario(@RequestBody UserDTO usuario) {
        return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> editarUsuario(@PathVariable Long id, @RequestBody UserDTO usuarioDto) {
        return ResponseEntity.ok(userService.actualizarUsuario(id, usuarioDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<AccessDTO>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerHistorial(id));
    }

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return  ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // quitar "Bearer "
            String username = jwtService.getUsernameFromToken(token); // extrae username del JWT
            userServiceImpl.registrarLogout(username); // método que guardará el acceso con estado "CERRADO"
            return ResponseEntity.ok("Sesión cerrada correctamente");
        }
        return ResponseEntity.badRequest().body("Token inválido");
    }

}
