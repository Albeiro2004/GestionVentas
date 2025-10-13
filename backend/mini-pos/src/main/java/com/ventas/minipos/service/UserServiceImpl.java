package com.ventas.minipos.service;

import com.ventas.minipos.domain.User;
import com.ventas.minipos.domain.Access;
import com.ventas.minipos.domain.Role;
import com.ventas.minipos.dto.UserDTO;
import com.ventas.minipos.dto.AccessDTO;
import com.ventas.minipos.exception.BusinessException;
import com.ventas.minipos.repo.UserRepository;
import com.ventas.minipos.repo.AccessRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final UserMapper userMapper;

    @Autowired
    private HttpServletRequest req;

    @Override
    public List<UserDTO> listarUsuarios() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDTO guardarUsuario(UserDTO usuarioDTO) {
        User user = new User();
        user.setUsername(usuarioDTO.username());
        user.setName(usuarioDTO.name());

        // Solo setea rol si viene en el DTO
        if (usuarioDTO.role() != null) {
            user.setRole(Role.valueOf(usuarioDTO.role()));
        }

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public void eliminarUsuario(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameActual = auth.getName();

        User usuarioActual = userRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new BusinessException("User not found!"));

        if (usuarioActual.getId().equals(id)){
            throw new BusinessException("¡No Puedes Eliminar tu Usuario!");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<AccessDTO> obtenerHistorial(Long id) {
        List<Access> accesos = accessRepository.findTop10ByUser_IdOrderByFechaDesc(id);
        return accesos.stream()
                .map(userMapper::toAccessDTO)
                .toList();
    }

    @Override
    public UserDTO obtenerUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO actualizarUsuario(Long id, UserDTO usuarioDto) {
        User usuarioExistente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualiza solo nombre y rol
        if (usuarioDto.name() != null) {
            usuarioExistente.setName(usuarioDto.name());
        }
        if (usuarioDto.role() != null) {
            usuarioExistente.setRole(Role.valueOf(usuarioDto.role()));
        }

        User actualizado = userRepository.save(usuarioExistente);
        return userMapper.toDto(actualizado);
    }

    public void registrarAcceso(User user, String estado, String ip) {
        Access acceso = new Access();
        acceso.setUser(user);
        acceso.setEstado(estado); // e.g., "LOGIN" o "LOGOUT"
        acceso.setFecha(LocalDateTime.now());
        acceso.setIp(ip);

        accessRepository.save(acceso);
    }
    public void registrarLogout(String username) {

        String ip = req.getRemoteAddr();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Access access = new Access();
        access.setUser(user);
        access.setFecha(LocalDateTime.now());
        access.setIp(ip);
        access.setEstado("LOGOUT");

        accessRepository.save(access);
    }


}
