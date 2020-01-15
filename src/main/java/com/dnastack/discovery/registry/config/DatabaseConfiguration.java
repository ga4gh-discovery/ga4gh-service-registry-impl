package com.dnastack.discovery.registry.config;

import com.dnastack.discovery.registry.model.OrganizationModel;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceType;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.argument.NullArgument;
import org.jdbi.v3.core.argument.ObjectArgument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.transaction.SerializableTransactionRunner;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Jdbi jdbi() {
        return Jdbi.create(new TransactionAwareDataSourceProxy(dataSource))
                .setTransactionHandler(new SerializableTransactionRunner())
                .registerRowMapper(BeanMapper.factory(ServiceInstanceModel.class))
                .registerColumnMapper(new ServiceTypeColumnMapper())
                .registerArgument(new ServiceTypeArgumentFactory())
                .registerRowMapper(BeanMapper.factory(OrganizationModel.class))
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new Jackson2Plugin());
    }

    public static class ServiceTypeColumnMapper implements ColumnMapper<ServiceType> {

        @Override
        public ServiceType map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
            return ServiceType.fromString(r.getString(columnNumber));
        }

        @Override
        public ServiceType map(ResultSet r, String columnName, StatementContext ctx) throws SQLException {
            return ServiceType.fromString(r.getString(columnName));
        }
    }

    private static class ServiceTypeArgumentFactory implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
            if (type.getTypeName().equals(ServiceType.class.getName())) {
                return Optional.of(ObjectArgument.of(value, Types.VARCHAR));
            }
            return Optional.empty();
        }
    }
}