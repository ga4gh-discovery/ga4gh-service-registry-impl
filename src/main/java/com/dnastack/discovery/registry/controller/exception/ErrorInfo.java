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

    private ZonedDateTime timestamp;
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String serviceInstanceId;

    private List<Error> errors;

}
