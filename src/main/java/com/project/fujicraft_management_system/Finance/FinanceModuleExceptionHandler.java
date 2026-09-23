package com.project.fujicraft_management_system.Finance;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.project.fujicraft_management_system.Finance")
public class FinanceModuleExceptionHandler {
    @ExceptionHandler(FinanceModuleException.class)
    ResponseEntity<Map<String, Object>> finance(FinanceModuleException exception, HttpServletRequest request) {
        return error(exception.getStatus(), exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        var errors = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message, HttpServletRequest request,
            Map<String, String> errors) {
        var body = new LinkedHashMap<String, Object>();
        body.put("status", status.value());
        body.put("message", message);
        body.put("errors", errors == null ? Map.of() : errors);
        body.put("traceId", request.getHeader("X-Trace-Id") == null ? java.util.UUID.randomUUID().toString()
                : request.getHeader("X-Trace-Id"));
        body.put("timestamp", Instant.now());
        return ResponseEntity.status(status).body(body);
    }
}