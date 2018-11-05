package com.dnastack.discovery.registry.repository;

import static java.util.Objects.isNull;

import com.dnastack.discovery.registry.domain.QServiceNodeEntity;
import com.querydsl.core.BooleanBuilder;

public final class ServiceNodePredicates {

    private static final QServiceNodeEntity serviceNode = QServiceNodeEntity.serviceNodeEntity;

    private ServiceNodePredicates() {
    }

    public static BooleanBuilder filterByName(String name) {
        BooleanBuilder builder = new BooleanBuilder();

        if (isNull(name) || name.isEmpty()) {
            return builder;
        }

        builder
            .and(serviceNode.name.containsIgnoreCase(name));

        return builder;
    }

    public static BooleanBuilder filterByDescription(String description) {
        BooleanBuilder builder = new BooleanBuilder();

        if (isNull(description) || description.isEmpty()) {
            return builder;
        }

        builder
            .and(serviceNode.description.containsIgnoreCase(description));

        return builder;
    }

    public static BooleanBuilder filterByAlias(String alias) {
        BooleanBuilder builder = new BooleanBuilder();

        if (isNull(alias) || alias.isEmpty()) {
            return builder;
        }

        builder
            .and(serviceNode.aliases.contains(alias));

        return builder;
    }
}
