package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, String> {

    @Query("SELECT DISTINCT si.type FROM ServiceInstance si")
    Stream<String> findAllDistinctTypes();
    Stream<ServiceInstance> findAllByType(String type);
    Optional<ServiceInstance> findOneById(String id);
    Optional<ServiceInstance> findOneByNameAndType(String name, String type);

}
