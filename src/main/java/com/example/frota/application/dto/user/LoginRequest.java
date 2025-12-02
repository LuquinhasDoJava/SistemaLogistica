package com.example.frota.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        @Size(max = 100, message = "Email não pode exceder 100 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha

) {}