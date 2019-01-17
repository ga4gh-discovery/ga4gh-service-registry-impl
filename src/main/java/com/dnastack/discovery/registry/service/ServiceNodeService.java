package com.dnastack.discovery.registry.service;

import static com.dnastack.discovery.registry.mapper.ServiceNodeMapper.map;
import static com.dnastack.discovery.registry.mapper.ServiceNodeMapper.reverseMap;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByAlias;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByDescription;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByName;
import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.domain.ServiceEntity;
import com.dnastack.discovery.registry.mapper.ServiceNodeMapper;
import com.dnastack.discovery.registry.domain.ServiceModel;
import com.dnastack.discovery.registry.repository.ServiceNodeRepository;
import java.time.ZonedDateTime;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServiceNodeService {

    private ServiceNodeRepository repository;

    @Inject
    public ServiceNodeService(ServiceNodeRepository repository) {
        this.repository = repository;
    }

    public ServiceModel save(ServiceModel model) {
        model.setCreatedAt(ZonedDateTime.now());
        return map(repository.save(reverseMap(model)));
    }

    public Page<ServiceModel> getNodes(String query, Pageable pageable) {
        Page<ServiceEntity> page = repository.findAll(filterByName(query)
                .or(filterByDescription(query))
                .or(filterByAlias(query)),
            pageable);
        return getNodes(pageable, page);
    }

    private Page<ServiceModel> getNodes(Pageable pageable, Page<ServiceEntity> page) {
        List<ServiceModel> content = page.getContent().stream()
            .map(ServiceNodeMapper::map)
            .collect(toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public ServiceModel getNodeById(String nodeId) {
        return repository.findById(nodeId)
            .map(ServiceNodeMapper::map)
            .orElseThrow(ServiceNodeNotFoundException::new);
    }

}
