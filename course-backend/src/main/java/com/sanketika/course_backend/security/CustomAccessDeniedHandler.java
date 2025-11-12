package com.sanketika.course_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.course_backend.utils.ApiEnvelope;
import com.sanketika.course_backend.utils.ApiResult;
import com.sanketika.course_backend.utils.Params;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiEnvelope<Object> envelope = new ApiEnvelope<>();
        envelope.setId(UUID.randomUUID().toString());
        envelope.setVer("1.0");
        envelope.setTs(LocalDateTime.now().toString());
        envelope.setResponseCode("403");

        Params params = new Params();
        params.setErr("FORBIDDEN");
        params.setStatus("failure");
        params.setErrmsg("You do not have permission to access this resource.");
        envelope.setParams(params);

        ApiResult<Object> result = new ApiResult<>();
        result.setMessage("Forbidden access");
        envelope.setResult(result);

        new ObjectMapper().writeValue(response.getOutputStream(), envelope);
    }
}