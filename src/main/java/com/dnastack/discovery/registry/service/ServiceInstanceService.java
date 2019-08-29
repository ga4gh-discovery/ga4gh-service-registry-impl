package com.dnastack.discovery.registry.service;

import com.dnastack.discovery.registry.domain.Organization;
import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.mapper.OrganizationMapper;
import com.dnastack.discovery.registry.mapper.ServiceInstanceMapper;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.repository.OrganizationRepository;
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

    private ServiceInstanceRepository serviceRepository;
    private OrganizationRepository organizationRepository;

    @Inject
    public ServiceInstanceService(ServiceInstanceRepository serviceRepository, OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
        this.serviceRepository = serviceRepository;
    }

    public ServiceInstanceModel registerInstance(ServiceInstanceRegistrationRequestModel registrationModel) {
        ServiceInstance serviceInstance = ServiceInstanceMapper.reverseMap(registrationModel);
        ZonedDateTime now = ZonedDateTime.now();
        serviceInstance.setCreatedAt(now);
        serviceInstance.setUpdatedAt(now);

        Optional<ServiceInstanceModel> existingInstance = getInstanceByNameAndType(serviceInstance.getName(),
                                                                                   serviceInstance.getType());
        if (existingInstance.isPresent()) {
            throw new ServiceInstanceExistsException("Service instance (ID " + existingInstance.get()
                    .getId() + ") with given name and type already exists");
        }

        final Optional<Organization> organization = organizationRepository.findByName(registrationModel.getOrganization()
                                                                                        .getName());

        organization.ifPresentOrElse(o -> serviceInstance.setOrganization(o), () -> {
            final Organization newOrganization = organizationRepository.save(OrganizationMapper.reverseMap(registrationModel.getOrganization()));
            serviceInstance.setOrganization(newOrganization);
        });

        return map(serviceRepository.save(serviceInstance));
    }

    public ServiceInstanceModel replaceInstance(String id, ServiceInstanceModel patch) {
        ServiceInstanceModel existingInstance = getInstanceById(id);
        patch.setId(id);
        patch.setCreatedAt(existingInstance.getCreatedAt());
        patch.setUpdatedAt(ZonedDateTime.now());
        return ServiceInstanceMapper.map(serviceRepository.save(ServiceInstanceMapper.reverseMap(patch)));
    }

    public void deregisterInstanceById(String id) {
        serviceRepository.deleteById(id);
    }

    public List<ServiceInstanceModel> getInstances() {
        return serviceRepository.findAll().stream().map(ServiceInstanceMapper::map).collect(toList());
    }

    public ServiceInstanceModel getInstanceById(String id) {
        return serviceRepository.findOneById(id)
                .map(ServiceInstanceMapper::map)
                .orElseThrow(ServiceInstanceNotFoundException::new);
    }

    public Optional<ServiceInstanceModel> getInstanceByNameAndType(String name, String type) {
        return serviceRepository.findOneByNameAndType(name, type).map(ServiceInstanceMapper::map);
    }

    public List<String> getTypes() {
        return serviceRepository.findAllDistinctTypes().collect(toList());
    }

}