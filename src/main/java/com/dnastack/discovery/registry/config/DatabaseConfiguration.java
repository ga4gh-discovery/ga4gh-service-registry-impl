package com.dnastack.discovery.registry.config;

import com.dnastack.discovery.registry.model.OrganizationModel;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.transaction.SerializableTransactionRunner;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Jdbi jdbi() {
        return Jdbi.create(new TransactionAwareDataSourceProxy(dataSource))
                .setTransactionHandler(new SerializableTransactionRunner())
                .registerRowMapper(BeanMapper.factory(ServiceInstanceModel.class))
                .registerRowMapper(BeanMapper.factory(OrganizationModel.class))
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new Jackson2Plugin());
    }
}