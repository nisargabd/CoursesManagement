package com.sanketika.course_backend.authDto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
