package com.sanketika.course_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.course_backend.utils.ApiEnvelope;
import com.sanketika.course_backend.utils.ApiResult;
import com.sanketika.course_backend.utils.Params;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiEnvelope<Object> envelope = new ApiEnvelope<>();
        envelope.setId(UUID.randomUUID().toString());
        envelope.setVer("1.0");
        envelope.setTs(LocalDateTime.now().toString());
        envelope.setResponseCode("401");

        Params params = new Params();
        params.setErr("UNAUTHORIZED");
        params.setStatus("failure");
        params.setErrmsg("Invalid or expired token. Please login again.");
        envelope.setParams(params);

        ApiResult<Object> result = new ApiResult<>();
        result.setMessage("Unauthorized access");
        envelope.setResult(result);

        new ObjectMapper().writeValue(response.getOutputStream(), envelope);
    }
}