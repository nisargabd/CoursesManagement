package com.sanketika.course_backend.services;

import com.sanketika.course_backend.authDto.AuthResponse;
import com.sanketika.course_backend.authDto.LoginRequest;
import com.sanketika.course_backend.authDto.RegisterRequest;
import com.sanketika.course_backend.entity.User;
import com.sanketika.course_backend.repositories.UserRepository;
import com.sanketika.course_backend.security.JwtService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String getCurrentUserRole() {
    var context = SecurityContextHolder.getContext();

    if (context == null || context.getAuthentication() == null) {
        return null;
    }

    Object principal = context.getAuthentication().getPrincipal();

    if (principal instanceof UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority()) // e.g., "ADMIN"
                .orElse(null);
    }

    return null;
}
public String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // email
        }
        return null;
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