package com.example.frota.api.controller.protegido;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protected")
public class ProtegidoController {

    /**
     * Endpoint de teste. Só acessível com Token JWT válido.
     */
    @GetMapping
    public ResponseEntity<String> getProtectedData(Authentication authentication) {
        String username = authentication.name();
        return ResponseEntity.ok("Bem-vindo, " + username + "! Você acessou uma rota protegida com sucesso.");
    }
}
