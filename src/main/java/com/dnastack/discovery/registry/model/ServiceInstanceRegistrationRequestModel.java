package com.dnastack.discovery.registry.model;

import com.dnastack.discovery.registry.domain.ServiceInstanceType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceRegistrationRequestModel {

    private String url;
    private ServiceInstanceType type;
    private String contactUrl;
    private String name;
    private String description;
    private List<String> aliases;
    private Map<String, String> metadata;

}
