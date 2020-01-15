package com.dnastack.discovery.registry.controller.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@ToString
@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {

    private int status;
    private String title;
    private String detail;

    /**
     * Additional property not in the specification. Needed by Beacon Network so it can fall back from
     * POST to PUT when updating existing registrations.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String serviceInstanceId;
}
