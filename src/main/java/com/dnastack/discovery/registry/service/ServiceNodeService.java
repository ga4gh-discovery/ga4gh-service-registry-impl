package com.dnastack.discovery.registry.service;

import static com.dnastack.discovery.registry.mapper.ServiceNodeMapper.map;
import static com.dnastack.discovery.registry.mapper.ServiceNodeMapper.reverseMap;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByAlias;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByDescription;
import static com.dnastack.discovery.registry.repository.ServiceNodePredicates.filterByName;
import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.domain.ServiceNodeEntity;
import com.dnastack.discovery.registry.mapper.ServiceNodeMapper;
import com.dnastack.discovery.registry.model.ServiceNode;
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

    public ServiceNode save(ServiceNode model) {
        model.setCreated(ZonedDateTime.now());
        return map(repository.save(reverseMap(model)));
    }

    public Page<ServiceNode> getNodes(String query, Pageable pageable) {
        Page<ServiceNodeEntity> page = repository.findAll(filterByName(query)
                .or(filterByDescription(query))
                .or(filterByAlias(query)),
            pageable);
        return getNodes(pageable, page);
    }

    private Page<ServiceNode> getNodes(Pageable pageable, Page<ServiceNodeEntity> page) {
        List<ServiceNode> content = page.getContent().stream()
            .map(ServiceNodeMapper::map)
            .collect(toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public ServiceNode getNodeById(String nodeId) {
        return repository.findById(nodeId)
            .map(ServiceNodeMapper::map)
            .orElseThrow(ServiceNodeNotFoundException::new);
    }

}
