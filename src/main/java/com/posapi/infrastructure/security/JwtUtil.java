package com.posapi.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component("customJwtUtil")
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // en milisegundos

    // Generar token para un usuario
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Generar token con claims adicionales
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                     // 🔥 Antes .setClaims()
                .subject(subject)                                   // 🔥 Antes .setSubject()
                .issuedAt(new Date(System.currentTimeMillis()))     // 🔥 Antes .setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 🔥 Antes .setExpiration()
                .signWith(getSignKey())                             // 🔥 Antes requería SignatureAlgorithm.HS256 de forma explícita
                .compact();
    }

    // Validar token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extraer nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer fecha de expiración del token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer un claim específico del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()                                        // 🔥 Solución: .parser() reemplaza a .parserBuilder()
                .verifyWith(getSignKey())                           // 🔥 Solución: .verifyWith() reemplaza a .setSigningKey()
                .build()
                .parseSignedClaims(token)                           // 🔥 Solución: .parseSignedClaims() reemplaza a .parseClaimsJws()
                .getPayload();                                      // 🔥 Solución: .getPayload() reemplaza a .getBody()
    }

    // Verificar si el token ha expirado
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Obtener la clave de firma
    private SecretKey getSignKey() {                                // 🔥 Cambiado de Key a SecretKey (Requerido por la nueva API)
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}