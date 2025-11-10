package com.sanketika.course_backend.authDto;

import lombok.Data;

@Data
public class AuthResponse {
    String token;
    String role;
    String username;

    public AuthResponse(String token, String role, String username) {
        this.token= token;
        this.role= role;
        this.username= username;

    }
}
