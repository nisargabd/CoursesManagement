package com.sanketika.course_backend.exceptions;

import com.sanketika.course_backend.utils.ApiEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // Handle custom "not found" errors (like missing course or unit)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiEnvelope<Void> response = new ApiEnvelope<>();
        response.setId("api.error");
        response.setVer("v1");
        response.setTs(Instant.now().toString());
        response.getParams().setMsgid(UUID.randomUUID().toString());
        response.getParams().setStatus("failed");
        response.getParams().setErr("RESOURCE_NOT_FOUND");
        response.getParams().setErrmsg(ex.getMessage());
        response.setResponseCode("RESOURCE_NOT_FOUND");
        response.setResult(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        logger.warn("URL not found: {}", ex.getRequestURL());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "The requested URL " + ex.getRequestURL() + " was not found.");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append(". ")
        );

        ApiEnvelope<Void> response = new ApiEnvelope<>();
        response.setId("api.error");
        response.setVer("v1");
        response.setTs(Instant.now().toString());
        response.getParams().setMsgid(UUID.randomUUID().toString());
        response.getParams().setStatus("failed");
        response.getParams().setErr("BAD_REQUEST");
        response.getParams().setErrmsg(errorMessage.toString().trim());
        response.setResponseCode("BAD_REQUEST");
        response.setResult(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle unexpected runtime errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleRuntimeException(RuntimeException ex) {
        ApiEnvelope<Void> response = new ApiEnvelope<>();
        response.setId("api.error");
        response.setVer("v1");
        response.setTs(Instant.now().toString());
        response.getParams().setMsgid(UUID.randomUUID().toString());
        response.getParams().setStatus("failed");
        response.getParams().setErr("INTERNAL_SERVER_ERROR");
        response.getParams().setErrmsg(ex.getMessage());
        response.setResponseCode("INTERNAL_SERVER_ERROR");
        response.setResult(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Handle all other exceptions (generic fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleGenericException(Exception ex) {
        ApiEnvelope<Void> response = new ApiEnvelope<>();
        response.setId("api.error");
        response.setVer("v1");
        response.setTs(Instant.now().toString());
        response.getParams().setMsgid(UUID.randomUUID().toString());
        response.getParams().setStatus("failed");
        response.getParams().setErr("BAD_REQUEST");
        response.getParams().setErrmsg(ex.getMessage());
        response.setResponseCode("BAD_REQUEST");
        response.setResult(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
