package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/services")
public class ServiceInstanceController {

    private final ServiceInstanceService service;

    @Inject
    public ServiceInstanceController(ServiceInstanceService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity registerServiceInstance(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm,
            @RequestBody ServiceInstanceModel registrationRequest) throws BindException {
        ServiceInstanceModel newInstance = service.registerInstance(realm, registrationRequest);
        ServletUriComponentsBuilder selfUri = ServletUriComponentsBuilder.fromCurrentRequestUri();
        selfUri.pathSegment("{serviceId}");
        return ResponseEntity.created(selfUri.build(newInstance.getId())).body(newInstance);
    }

    @PutMapping(value = "/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity replaceServiceInstance(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm,
            @PathVariable("serviceId") String serviceId,
            @RequestBody ServiceInstanceModel patch) {
        return ResponseEntity.status(HttpStatus.OK).body(service.replaceInstance(realm, serviceId, patch));
    }

    @DeleteMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deregisterServiceInstanceById(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm,
            @PathVariable("serviceId") String serviceId) {
        service.deregisterInstanceById(realm, serviceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceById(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm,
            @PathVariable("serviceId") String serviceId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getInstanceById(realm, serviceId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstances(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getInstances(realm));
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getServiceInstanceTypes(
            @RequestHeader(name="Service-Registry-Realm", defaultValue = "default") String realm) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTypes(realm));
    }

}
