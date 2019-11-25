package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.model.OrganizationModel;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository {

    @SqlQuery("SELECT * FROM organization o WHERE o.realm = :realm AND o.name = :name")
    Optional<OrganizationModel> findByName(String realm, String name);

    @SqlUpdate("INSERT INTO organization (realm, id, name, url) VALUES (:realm, :org.id, :org.name, :org.url)")
    void save(@Bind("realm") String realm, @BindBean("org") OrganizationModel organization);
}
