package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.service.ServiceNodeService;
import javax.inject.Inject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/nodes")
public class ServiceNodeController {

    private ServiceNodeService serviceNodeService;

    @Inject
    public ServiceNodeController(ServiceNodeService serviceNodeService) {
        this.serviceNodeService = serviceNodeService;
    }

    @GetMapping(value = "/{nodeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getNodeById(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(serviceNodeService.getNodeById(nodeId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getNodes(@RequestParam(required = false) String query, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(serviceNodeService.getNodes(query, pageable));
    }

}
