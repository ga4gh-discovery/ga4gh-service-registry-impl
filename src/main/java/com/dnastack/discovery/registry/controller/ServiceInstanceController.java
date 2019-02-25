package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.service.ServiceInstanceService;
import javax.inject.Inject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/services")
public class ServiceInstanceController {

    private ServiceInstanceService serviceNodeService;

    @Inject
    public ServiceInstanceController(ServiceInstanceService serviceNodeService) {
        this.serviceNodeService = serviceNodeService;
    }

    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceById(@PathVariable("serviceId") String serviceId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(serviceNodeService.getNodeById(serviceId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstances(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(serviceNodeService.getNodes(pageable));
    }

}
