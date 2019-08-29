package com.dnastack.discovery.registry.controller.exception;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError implements Error {

    private String code;
    private String message;
    private String field;
    private Object rejectedValue;

}
