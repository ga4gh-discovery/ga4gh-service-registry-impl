package com.dnastack.discovery.registry.service;

import com.dnastack.discovery.registry.controller.exception.HasServiceInstanceId;

public class ServiceInstanceExistsException extends RuntimeException implements HasServiceInstanceId {

    private final String serviceInstanceId;

    public ServiceInstanceExistsException(String serviceInstanceId, String message) {
        super(message);
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }
}
