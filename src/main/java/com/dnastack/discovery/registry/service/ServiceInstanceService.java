package com.dnastack.discovery.registry.service;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.mapper.ServiceInstanceMapper;
import com.dnastack.discovery.registry.model.PaginatedServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.repository.ServiceInstanceRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.dnastack.discovery.registry.mapper.ServiceInstanceMapper.map;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ServiceInstanceService {

    private ServiceInstanceRepository repository;

    @Inject
    public ServiceInstanceService(ServiceInstanceRepository repository) {
        this.repository = repository;
    }

    public ServiceInstanceModel registerInstance(ServiceInstanceRegistrationRequestModel registrationModel) {
        ServiceInstance serviceInstance = ServiceInstanceMapper.reverseMap(registrationModel);
        ZonedDateTime now = ZonedDateTime.now();
        serviceInstance.setCreatedAt(now);
        serviceInstance.setUpdatedAt(now);

        Optional<ServiceInstanceModel> existingInstance = getInstanceByNameAndType(serviceInstance.getName(), serviceInstance.getType());
        if (existingInstance.isPresent()) {
            throw new ServiceInstanceExistsException("Service instance (ID " + existingInstance.get().getId() + ") with given name and type already exists");
        }

        return map(repository.save(serviceInstance));
    }

    public ServiceInstanceModel replaceInstance(String id, ServiceInstanceModel patch) {
        ServiceInstanceModel existingInstance = getInstanceById(id);
        patch.setId(id);
        patch.setCreatedAt(existingInstance.getCreatedAt());
        patch.setUpdatedAt(ZonedDateTime.now());
        return ServiceInstanceMapper.map(repository.save(ServiceInstanceMapper.reverseMap(patch)));
    }

    public void deregisterInstanceById(String id) {
        repository.deleteById(id);
    }

    public PaginatedServiceInstanceModel getInstances(String page) {
        List<ServiceInstanceModel> content = repository.findAll().stream()
                .map(ServiceInstanceMapper::map)
                .collect(toList());
        return PaginatedServiceInstanceModel.builder()
                .content(content)
                .build();
    }

    public ServiceInstanceModel getInstanceById(String id) {
        return repository.findOneById(id)
                .map(ServiceInstanceMapper::map)
                .orElseThrow(ServiceInstanceNotFoundException::new);
    }

    public Optional<ServiceInstanceModel> getInstanceByNameAndType(String name, String type) {
        return repository.findOneByNameAndType(name, type)
                .map(ServiceInstanceMapper::map);
    }

    public List<String> getTypes() {
        return repository.findAllDistinctTypes()
                .collect(toList());
    }

}