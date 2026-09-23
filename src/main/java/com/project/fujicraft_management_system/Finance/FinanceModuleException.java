package com.project.fujicraft_management_system.Finance;

import org.springframework.http.HttpStatus;

public class FinanceModuleException extends RuntimeException {
    private final HttpStatus status;

    public FinanceModuleException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}