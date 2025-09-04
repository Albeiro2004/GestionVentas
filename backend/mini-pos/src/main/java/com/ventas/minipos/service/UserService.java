package com.ventas.minipos.service;

import com.ventas.minipos.dto.AccessDTO;
import com.ventas.minipos.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> listarUsuarios();
    UserDTO guardarUsuario(UserDTO usuario);
    void eliminarUsuario(Long id);
    List<AccessDTO> obtenerHistorial(Long id);
    UserDTO obtenerUsuario(Long id);
    UserDTO actualizarUsuario(Long id, UserDTO usuarioDto);
}
