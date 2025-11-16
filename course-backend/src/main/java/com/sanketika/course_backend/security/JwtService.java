package com.sanketika.course_backend.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.private-key}") String privateKeyPath,
            @Value("${app.jwt.public-key}") String publicKeyPath,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) throws Exception {

        this.privateKey = PemUtils.loadPrivateKey(privateKeyPath);
        this.publicKey = PemUtils.loadPublicKey(publicKeyPath);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, String role, String username) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .addClaims(Map.of(
                        "role", role,
                        "username", username
                ))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
