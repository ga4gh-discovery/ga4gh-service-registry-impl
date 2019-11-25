package com.dnastack.discovery.registry.repository;

import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapperFactory;
import org.jdbi.v3.sqlobject.config.RegisterRowMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceInstanceRepository {

    @SqlQuery("SELECT si.*, o.id AS org_id, o.name AS org_name, o.url AS org_url" +
            " FROM service_instance si" +
            " LEFT OUTER JOIN organization o ON si.realm = o.realm AND si.organization_id = o.id" +
            " WHERE si.realm = :realm AND si.id = :id")
    Optional<ServiceInstanceModel> findById(String realm, String id);

    @SqlQuery("SELECT si.*, o.id AS org_id, o.name AS org_name, o.url AS org_url" +
            " FROM service_instance si" +
            " LEFT OUTER JOIN organization o ON si.realm = o.realm AND si.organization_id = o.id" +
            " WHERE si.realm = :realm AND si.name = :name AND si.type = :type")
    Optional<ServiceInstanceModel> findByNameAndType(String realm, String name, String type);

    @SqlQuery("SELECT DISTINCT si.type FROM service_instance si WHERE si.realm = :realm")
    List<String> findAllDistinctTypes(String realm);

    @SqlQuery("SELECT si.*, o.id AS org_id, o.name AS org_name, o.url AS org_url" +
            " FROM service_instance si" +
            " LEFT OUTER JOIN organization o ON si.realm = o.realm AND si.organization_id = o.id" +
            " WHERE si.realm = :realm")
    List<ServiceInstanceModel> findAll(String realm);

    @SqlUpdate("INSERT INTO service_instance (" +
            " realm," +
            " id," +
            " contact_url," +
            " created_at," +
            " description," +
            " documentation_url," +
            " environment," +
            " name," +
            " type," +
            " updated_at," +
            " url," +
            " version," +
            " organization_id," +
            " additional_properties_json" +
            ") VALUES (" +
            " :realm," +
            " :si.id," +
            " :si.contactUrl," +
            " :si.createdAt," +
            " :si.description," +
            " :si.documentationUrl," +
            " :si.environment," +
            " :si.name," +
            " :si.type," +
            " :si.updatedAt," +
            " :si.url," +
            " :si.version," +
            " :organizationId," +
            " :si.additionalProperties" +
            ")")
    void save(@Bind String realm, @Bind String organizationId, @BindBean("si") ServiceInstanceModel si);

    @SqlUpdate("UPDATE service_instance SET" +
            " contact_url = :si.contactUrl," +
            " created_at = :si.createdAt," +
            " description = :si.description," +
            " documentation_url = :si.documentationUrl," +
            " environment = :si.environment," +
            " name = :si.name," +
            " type = :si.type," +
            " updated_at = :si.updatedAt," +
            " url = :si.url," +
            " version = :si.version," +
            " organization_id = :si.organization.id," +
            " additional_properties_json = :si.additionalProperties" +
            " WHERE realm = :realm AND id = :si.id")
    void update(@Bind String realm, @BindBean("si") ServiceInstanceModel si);

    @SqlUpdate("DELETE FROM service_instance si WHERE si.realm = :realm AND si.id = :id")
    boolean delete(String realm, String id);
}
