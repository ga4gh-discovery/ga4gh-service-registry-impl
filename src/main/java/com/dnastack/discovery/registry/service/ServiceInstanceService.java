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

import static com.dnastack.discovery.registry.mapper.ServiceInstanceMapper.toDto;
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

    public ServiceInstanceModel registerInstance(
            String realm,
            ServiceInstanceRegistrationRequestModel registrationModel) {
        ServiceInstance serviceInstance = ServiceInstanceMapper.toEntity(realm, registrationModel);
        ZonedDateTime now = ZonedDateTime.now();
        serviceInstance.setCreatedAt(now);
        serviceInstance.setUpdatedAt(now);

        Optional<ServiceInstanceModel> existingInstance = getInstanceByNameAndType(
                realm,
                serviceInstance.getName(),
                serviceInstance.getType());
        if (existingInstance.isPresent()) {
            throw new ServiceInstanceExistsException(
                    "Service instance (ID " + existingInstance.get().getId() + ")" +
                            " with given name and type already exists");
        }

        final Optional<Organization> organization = organizationRepository.findByKeyRealmAndName(
                realm,
                registrationModel.getOrganization().getName());

        if (organization.isPresent()) {
            serviceInstance.setOrganization(organization.get());
        } else {
            Organization newOrganization = OrganizationMapper.toEntity(realm, registrationModel.getOrganization());
            newOrganization.setKey(Organization.Key.inRealm(realm));
            newOrganization = organizationRepository.save(newOrganization);
            serviceInstance.setOrganization(newOrganization);
        }

        return toDto(serviceRepository.save(serviceInstance));
    }

    public ServiceInstanceModel replaceInstance(String realm, String id, ServiceInstanceModel patch) {
        ServiceInstanceModel existingInstance = getInstanceById(realm, id);
        patch.setId(id);
        patch.setCreatedAt(existingInstance.getCreatedAt());
        patch.setUpdatedAt(ZonedDateTime.now());
        patch.getAdditionalProperties().putAll(existingInstance.getAdditionalProperties());
        return ServiceInstanceMapper.toDto(serviceRepository.save(ServiceInstanceMapper.toEntity(realm, patch)));
    }

    public void deregisterInstanceById(String realm, String id) {
        serviceRepository.deleteById(new ServiceInstance.Key(realm, id));
    }

    public List<ServiceInstanceModel> getInstances(String realm) {
        return serviceRepository.findByKeyRealm(realm).stream().map(ServiceInstanceMapper::toDto).collect(toList());
    }

    public ServiceInstanceModel getInstanceById(String realm, String id) {
        return serviceRepository.findById(new ServiceInstance.Key(realm, id))
                .map(ServiceInstanceMapper::toDto)
                .orElseThrow(ServiceInstanceNotFoundException::new);
    }

    public Optional<ServiceInstanceModel> getInstanceByNameAndType(String realm, String name, String type) {
        return serviceRepository.findOneByKeyRealmAndNameAndType(realm, name, type).map(ServiceInstanceMapper::toDto);
    }

    public List<String> getTypes(String realm) {
        return serviceRepository.findAllDistinctTypes(realm).collect(toList());
    }

}