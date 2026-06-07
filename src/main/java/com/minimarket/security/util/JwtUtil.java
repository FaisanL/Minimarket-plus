package com.minimarket.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey";
    private static final long EXPIRATION_TIME = 86400000; // 1 dia en milisegundos
    
    private Key getSigningKey() { // Genera una clave de firma a partir de la cadena secreta
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); 
    }

    public String generateToken(String username) { // Genera un token JWT para un nombre de usuario dado
        return Jwts.builder()
                .setSubject(username) // Establece el sujeto del token como el nombre de usuario
                .setIssuedAt(new Date()) // Establece la fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Establece la fecha de expiración del token
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token con la clave de firma y el algoritmo HS256
                .compact(); // Compila el token en una cadena compacta
    }

    public String extractUsername(String token) { // Extrae el nombre de usuario del token JWT
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Establece la clave de firma para validar el token
                .build()
                .parseClaimsJws(token) // Analiza el token y obtiene las reclamaciones
                .getBody()
                .getSubject(); // Devuelve el sujeto del token, que es el nombre de usuario
    }

    public boolean validateToken(String token) { // Valida el token JWT
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Establece la clave de firma para validar el token
                .build()
                .parseClaimsJws(token); // Analiza el token y verifica su validez
            return true; // Si no se lanza ninguna excepción, el token es válido
        } catch (JwtException e) {
            return false; // Si se lanza una excepción, el token no es válido
        }
    }





}
