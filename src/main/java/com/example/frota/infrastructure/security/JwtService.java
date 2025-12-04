package com.example.frota.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.chave}")
    private String CHAVE_JWT;
    
    @Value("${jwt.tempo:86400000}")
    private long tempoExpirar;

    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public <T> T extrairClaim(String token, Function<Claims, T> resolvedorClaims) {
        final Claims claims = extrairTodosClaims(token);
        return resolvedorClaims.apply(claims);
    }

    public String extrairRole(String token) {
        return extrairClaim(token, claims -> claims.get("role", String.class));
    }

    public String extrairNome(String token) {
        return extrairClaim(token, claims -> claims.get("nome", String.class));
    }

    public Long extrairUserId(String token) {
        return extrairClaim(token, claims -> claims.get("id", Long.class));
    }

    public String gerarToken(String email, String role, Long id, String nome) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("nome", nome);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpirar))
                .signWith(getChave(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getChave())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarTokenExpirado(String token) {
        return extrairTempoExpiracao(token).before(new Date());
    }

    private Date extrairTempoExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getChave())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getChave() {
        byte[] keyBytes = Decoders.BASE64.decode(CHAVE_JWT);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}