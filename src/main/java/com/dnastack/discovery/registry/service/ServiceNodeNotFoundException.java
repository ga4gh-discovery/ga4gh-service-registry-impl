package com.dnastack.discovery.registry.service;

public class ServiceNodeNotFoundException extends RuntimeException {

    public ServiceNodeNotFoundException() {
    }

    public ServiceNodeNotFoundException(String message) {
        super(message);
    }

    public ServiceNodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
