package com.movie.booking.booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry-minutes:1440}")
    private int expiryMinutes;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role)
            .id(UUID.randomUUID().toString())
            .issuedAt(new Date(now))
            .expiration(new Date(now + (long) expiryMinutes * 60 * 1000))
            .signWith(key())
            .compact();
    }

    public Claims extractAll(String token) {
        return Jwts.parser().verifyWith(key()).build()
            .parseSignedClaims(token).getPayload();
    }

    public String extractUserId(String token) { return extractAll(token).getSubject(); }
    public String extractRole(String token)   { return extractAll(token).get("role", String.class); }

    public boolean isValid(String token) {
        try { extractAll(token); return true; } catch (Exception e) { return false; }
    }
}
