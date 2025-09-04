package com.ventas.minipos.service;

import com.ventas.minipos.domain.Access;
import com.ventas.minipos.domain.Role;
import com.ventas.minipos.domain.User;
import com.ventas.minipos.dto.AccessDTO;
import com.ventas.minipos.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    public User toEntity(UserDTO dto, User userExistente) {
        userExistente.setName(dto.name());
        if (dto.role() != null) {
            userExistente.setRole(Role.valueOf(dto.role()));
        }
        return userExistente;
    }


    public AccessDTO toAccessDTO(Access access) {
        return new AccessDTO(
                access.getId(),
                access.getFecha(),
                access.getIp(),
                access.getEstado()
        );
    }
}
