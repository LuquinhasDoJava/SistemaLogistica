package com.example.frota.application.dto.user;

import com.example.frota.domain.user.model.User;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime dtaRegistro,
        String role
) {

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getDtaRegistro(),
                user.getRole() != null ? user.getRole() : "USER"
        );
    }
}