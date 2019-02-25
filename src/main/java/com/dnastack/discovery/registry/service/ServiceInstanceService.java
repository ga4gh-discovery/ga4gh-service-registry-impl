package com.dnastack.discovery.registry.service;

import static com.dnastack.discovery.registry.mapper.ServiceInstanceMapper.map;
import static com.dnastack.discovery.registry.mapper.ServiceInstanceMapper.reverseMap;
import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.mapper.ServiceInstanceMapper;
import com.dnastack.discovery.registry.domain.ServiceInstanceModel;
import com.dnastack.discovery.registry.repository.ServiceInstanceRepository;
import java.time.ZonedDateTime;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServiceInstanceService {

    private ServiceInstanceRepository repository;

    @Inject
    public ServiceInstanceService(ServiceInstanceRepository repository) {
        this.repository = repository;
    }

    public ServiceInstanceModel save(ServiceInstanceModel model) {
        model.setCreatedAt(ZonedDateTime.now());
        return map(repository.save(reverseMap(model)));
    }

    public Page<ServiceInstanceModel> getNodes(Pageable pageable) {
        Page<ServiceInstance> page = repository.findAll(pageable);
        return getNodes(pageable, page);
    }

    private Page<ServiceInstanceModel> getNodes(Pageable pageable, Page<ServiceInstance> page) {
        List<ServiceInstanceModel> content = page.getContent().stream()
            .map(ServiceInstanceMapper::map)
            .collect(toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public ServiceInstanceModel getNodeById(String nodeId) {
        return repository.findById(nodeId)
            .map(ServiceInstanceMapper::map)
            .orElseThrow(ServiceInstanceNotFoundException::new);
    }

}
