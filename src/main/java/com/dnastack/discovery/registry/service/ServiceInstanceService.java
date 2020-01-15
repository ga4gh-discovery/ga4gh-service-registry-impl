package com.dnastack.discovery.registry.service;

import com.dnastack.discovery.registry.model.OrganizationModel;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceType;
import com.dnastack.discovery.registry.repository.OrganizationRepository;
import com.dnastack.discovery.registry.repository.ServiceInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class ServiceInstanceService {

    private final Jdbi jdbi;

    @Inject
    public ServiceInstanceService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    private void validate(ServiceInstanceModel si) throws BindException {
        BindException errors = new BindException(si, "service");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "organization", "required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", "required field");

        if (errors.hasErrors()) {
            throw errors;
        }
    }

    public ServiceInstanceModel registerInstance(
            String realm,
            ServiceInstanceModel newServiceInstance) throws BindException {
        validate(newServiceInstance);
        return jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);

            Optional<ServiceInstanceModel> existingInstance =
                    serviceRepository.findByNameAndType(
                            realm,
                            newServiceInstance.getName(),
                            newServiceInstance.getType().toString());

            if (existingInstance.isPresent()) {
                throw new ServiceInstanceExistsException(
                        existingInstance.get().getId(),
                        "Service instance (ID " + existingInstance.get().getId() + ")" +
                                " with given name and type already exists");
            }

            createOrResolveOrganization(handle, realm, newServiceInstance);

            ZonedDateTime now = ZonedDateTime.now();
            newServiceInstance.setId(UUID.randomUUID().toString());
            newServiceInstance.setCreatedAt(now);
            newServiceInstance.setUpdatedAt(now);

            serviceRepository.save(realm, newServiceInstance.getOrganization().getId(), newServiceInstance);
            return newServiceInstance;
        });
    }

    /**
     * Sets the {@code id} attribute of the given service's organization by setting it to the ID of
     * an existing organization in the same realm with the same name, or by creating a new organization
     * in the given realm and setting its ID.
     *
     * @param handle an active JDBI handle
     * @param realm the realm the service instance is in
     * @param serviceInstance the service instance whose organization should be updated.
     */
    private void createOrResolveOrganization(Handle handle, String realm, ServiceInstanceModel serviceInstance) {
        OrganizationRepository organizationRepository = handle.attach(OrganizationRepository.class);
        final Optional<String> organizationId = organizationRepository.findIdForName(
                realm,
                serviceInstance.getOrganization().getName());

        if (organizationId.isEmpty()) {
            OrganizationModel org = serviceInstance.getOrganization();
            org.setId(UUID.randomUUID().toString());
            log.debug("Creating new organization {} for this service instance", org.getId());
            organizationRepository.save(realm, org);
        } else {
            serviceInstance.getOrganization().setId(organizationId.get());
        }
    }

    public ServiceInstanceModel replaceInstance(String realm, String id, ServiceInstanceModel patch) {
        return jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);
            ServiceInstanceModel existingInstance = getInstanceById(realm, id);
            patch.setId(id);
            patch.setCreatedAt(existingInstance.getCreatedAt());
            patch.setUpdatedAt(ZonedDateTime.now());
            createOrResolveOrganization(handle, realm, patch);
            patch.getAdditionalProperties().putAll(existingInstance.getAdditionalProperties());
            log.debug("Replaced/updated service instance {}", id);
            serviceRepository.update(realm, patch);
            return patch;
        });
    }

    public void deregisterInstanceById(String realm, String id) {
        jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);
            if (!serviceRepository.delete(realm, id)) {
                throw new ServiceInstanceNotFoundException(id);
            }
            return null;
        });
    }

    public List<ServiceInstanceModel> getInstances(String realm) {
        return jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);
            return serviceRepository.findAll(realm);
        });
    }

    public ServiceInstanceModel getInstanceById(String realm, String id) {
        return jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);
            return serviceRepository.findById(realm, id)
                    .orElseThrow(ServiceInstanceNotFoundException::new);
        });
    }

    public List<ServiceType> getTypes(String realm) {
        return jdbi.withHandle(handle -> {
            ServiceInstanceRepository serviceRepository = handle.attach(ServiceInstanceRepository.class);
            return serviceRepository.findAllDistinctTypes(realm);
        });
    }

}