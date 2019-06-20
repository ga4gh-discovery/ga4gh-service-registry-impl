package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.domain.ServiceInstanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, String> {

    Stream<ServiceInstance> findAllByType(ServiceInstanceType type);
    Optional<ServiceInstance> findOneById(String id);
    Optional<ServiceInstance> findOneByNameAndType(String name, ServiceInstanceType type);

}
