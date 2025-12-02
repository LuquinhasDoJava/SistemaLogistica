package com.example.frota.application.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        Long id,
        String nome,
        String email,
        String token,        // Token JWT para autenticação
        String message,
        String role,         // Role do usuário
        String tokenType     // Tipo do token (ex: "Bearer")
) {

    public static AuthResponse loginSuccess(Long id, String nome, String email, String token, String role) {
        return new AuthResponse(
                id, nome, email,
                token,
                "Login realizado com sucesso",
                role,
                "Bearer"
        );
    }

    public static AuthResponse registerSuccess(Long id, String nome, String email) {
        return new AuthResponse(
                id, nome, email,
                null,
                "Usuário registrado com sucesso. Faça login para continuar.",
                "USER",
                null
        );
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(
                null, null, null,
                null,
                message,
                null,
                null
        );
    }
}