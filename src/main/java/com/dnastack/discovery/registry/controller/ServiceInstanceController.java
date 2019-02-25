package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.domain.ServiceInstanceType;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import javax.inject.Inject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/services")
public class ServiceInstanceController {

    private ServiceInstanceService service;

    @Inject
    public ServiceInstanceController(ServiceInstanceService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity registerServiceInstance(@RequestBody ServiceInstanceRegistrationRequestModel registrationRequest) {
        return ResponseEntity.status(HttpStatus.PROCESSING)
            .body(service.registerInstance(registrationRequest));
    }

    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceById(@PathVariable("serviceId") String serviceId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.getInstanceById(serviceId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstances(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.getInstances(pageable));
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceTypes() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ServiceInstanceType.values());
    }

}
