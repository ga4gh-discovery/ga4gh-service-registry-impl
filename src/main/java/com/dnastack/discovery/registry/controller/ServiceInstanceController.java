package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.domain.ServiceInstanceType;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import javax.inject.Inject;
import javax.websocket.server.PathParam;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registerInstance(registrationRequest));
    }

    @PutMapping(value = "/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity replaceServiceInstance(@PathVariable("serviceId") String serviceId, @RequestBody ServiceInstanceModel patch) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.replaceInstance(serviceId, patch));
    }

    @DeleteMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deregisterServiceInstanceById(@PathVariable("serviceId") String serviceId) {
        service.deregisterInstanceById(serviceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceById(@PathVariable("serviceId") String serviceId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getInstanceById(serviceId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstances(@RequestParam(value = "page", required = false) String page,
                                              @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getInstances(page, limit));
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceTypes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ServiceInstanceType.values());
    }

}
