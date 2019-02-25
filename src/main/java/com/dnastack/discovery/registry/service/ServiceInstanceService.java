package com.dnastack.discovery.registry.service;

import static com.dnastack.discovery.registry.mapper.ServiceInstanceMapper.map;
import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.mapper.ServiceInstanceMapper;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.repository.ServiceInstanceRepository;
import java.time.ZonedDateTime;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServiceInstanceService {

    private ServiceInstanceRepository repository;

    @Inject
    public ServiceInstanceService(ServiceInstanceRepository repository) {
        this.repository = repository;
    }

    public ServiceInstanceModel registerInstance(ServiceInstanceRegistrationRequestModel registrationModel) {
        ServiceInstance serviceInstance = ServiceInstanceMapper.reverseMap(registrationModel);
        serviceInstance.setCreatedAt(ZonedDateTime.now());

        // TODO: add test suite to test service instance prior persisting
        // TODO: async
        // TODO: contact service instance's owner re success/failure

        return map(repository.save(serviceInstance));
    }

    public Page<ServiceInstanceModel> getInstances(Pageable pageable) {
        Page<ServiceInstance> page = repository.findAll(pageable);
        return getInstances(pageable, page);
    }

    private Page<ServiceInstanceModel> getInstances(Pageable pageable, Page<ServiceInstance> page) {
        List<ServiceInstanceModel> content = page.getContent().stream()
            .map(ServiceInstanceMapper::map)
            .collect(toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public ServiceInstanceModel getInstanceById(String nodeId) {
        return repository.findById(nodeId)
            .map(ServiceInstanceMapper::map)
            .orElseThrow(ServiceInstanceNotFoundException::new);
    }

}
