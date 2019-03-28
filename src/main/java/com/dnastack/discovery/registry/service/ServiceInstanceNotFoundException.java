package com.dnastack.discovery.registry.service;

public class ServiceInstanceNotFoundException extends RuntimeException {

    public ServiceInstanceNotFoundException() {
    }

    public ServiceInstanceNotFoundException(String message) {
        super(message);
    }

    public ServiceInstanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
