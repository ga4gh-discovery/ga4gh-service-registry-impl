package com.dnastack.discovery.registry.domain;

import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import java.time.ZonedDateTime;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Health {

    private HealthStatus status;
    private ZonedDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    public HealthStatus getStatus() {
        return status;
    }

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}
