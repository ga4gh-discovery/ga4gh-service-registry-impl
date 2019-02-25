package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, String>, QuerydslPredicateExecutor<ServiceInstance> {

}
