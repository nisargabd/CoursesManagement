package com.sanketika.course_backend.services;

import com.sanketika.course_backend.authDto.AuthResponse;
import com.sanketika.course_backend.authDto.LoginRequest;
import com.sanketika.course_backend.authDto.RegisterRequest;
import com.sanketika.course_backend.entity.User;
import com.sanketika.course_backend.repositories.UserRepository;
import com.sanketika.course_backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(RegisterRequest req) {

        if (repo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(encoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        repo.save(user);
    }

    public AuthResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwt.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getUsername()
        );

        return new AuthResponse(token, user.getRole().name(), user.getUsername());
    }
}