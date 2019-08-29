package com.dnastack.discovery.registry.controller.exception;

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
    private List<Error> errors;

}
