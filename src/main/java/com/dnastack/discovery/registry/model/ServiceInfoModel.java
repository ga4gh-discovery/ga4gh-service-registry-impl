package com.dnastack.discovery.registry.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.service-info")
public class ServiceInfoModel extends ServiceInstanceModel {

}
