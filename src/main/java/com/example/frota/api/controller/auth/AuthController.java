package com.example.frota.api.controller.auth;

import com.example.frota.application.dto.user.AuthRequest;
import com.example.frota.application.dto.user.AuthResponse;
import com.example.frota.domain.user.model.User;
import com.example.frota.domain.user.repository.UserRepository;
import com.example.frota.infrastructure.security.JwtService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRequest authRequest) {
        boolean validaEmail = userRepository.findAll().stream()
            .anyMatch(u -> u.getEmail().equals(authRequest.email()));
            
        if (validaEmail) {
            return ResponseEntity.badRequest()
                .body("{\"erro\": \"Email já está em uso!\"}");
        }
        
        User novoUser = new User(
            authRequest.nome(),
            authRequest.email(),
            passwordEncoder.encode(authRequest.senha()),
            "ROLE_USER"
        );
        
        User userSalvo = userRepository.save(novoUser);
        
        String token = jwtService.gerarToken(
            userSalvo.getEmail(),
            userSalvo.getRole(),
            userSalvo.getId(),
            userSalvo.getNome()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AuthResponse.loginSuccess(
                userSalvo.getId(),
                userSalvo.getNome(),
                userSalvo.getEmail(),
                token,
                userSalvo.getRole()
            ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        try {
            User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(authRequest.email()))
                .findFirst()
                .orElse(null);
                
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"erro\": \"Email não encontrado\"}");
            }
            
            if (!passwordEncoder.matches(authRequest.senha(), user.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"erro\": \"Senha incorreta\"}");
            }
            
            String token = jwtService.gerarToken(
                user.getEmail(),
                user.getRole(),
                user.getId(),
                user.getNome()
            );
            
            return ResponseEntity.ok(AuthResponse.loginSuccess(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                token,
                user.getRole()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"erro\": \"Erro no servidor: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validaToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                .body("{\"erro\": \"Token não fornecido\"}");
        }
        
        String token = authHeader.substring(7);
        boolean valida = jwtService.validarToken(token);
        
        if (valida) {
            return ResponseEntity.ok("{\"mensagem\": \"Token válido\"}");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"erro\": \"Token inválido ou expirado\"}");
        }
    }
}