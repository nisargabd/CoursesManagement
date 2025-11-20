package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUser(){
        return ResponseEntity.ok(
            Map.of(
                "email", service.getCurrentUserEmail(),
                "role", service.getCurrentUserRole()
            )
        );
    }
}
