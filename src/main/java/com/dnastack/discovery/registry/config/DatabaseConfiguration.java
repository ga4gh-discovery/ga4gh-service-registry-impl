package com.dnastack.discovery.registry.config;

import com.dnastack.discovery.registry.model.OrganizationModel;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.json.JsonPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Jdbi jdbi() {
        return Jdbi.create(dataSource)
                .registerRowMapper(BeanMapper.factory(ServiceInstanceModel.class))
                .registerRowMapper(BeanMapper.factory(OrganizationModel.class))
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new Jackson2Plugin());
    }
}