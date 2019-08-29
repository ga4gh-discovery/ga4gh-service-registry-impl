package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.domain.Organization;
import com.dnastack.discovery.registry.domain.ServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findByName(String name);
}
