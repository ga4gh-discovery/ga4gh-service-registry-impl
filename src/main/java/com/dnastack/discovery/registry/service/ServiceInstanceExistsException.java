package com.dnastack.discovery.registry.service;

public class ServiceInstanceExistsException extends RuntimeException {

    public ServiceInstanceExistsException() {
    }

    public ServiceInstanceExistsException(String message) {
        super(message);
    }

}
