package com.sanketika.course_backend.authDto;

import com.sanketika.course_backend.security.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String phone;
    private String countryCode;
    private String password;
    private Role role;
}
