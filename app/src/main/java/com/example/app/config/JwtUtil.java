package com.example.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "miClaveSecretaSuperSegura123456789012345678901234567890";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos
    
    // Genera clave secreta
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    
    // Generar un token JWT
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                          // dueño
                .issuedAt(new Date())                    // Fecha de creación
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expira en 24h
                .signWith(getSigningKey())               // clave secreta
                .compact();
    }
    
    // Extraer el email del token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    // Validar si el token es válido
    public boolean isTokenValid(String token, String email) {
        String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
    
    // Verificar si el token expiró
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
    
    // Extraer todos los datos del token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
