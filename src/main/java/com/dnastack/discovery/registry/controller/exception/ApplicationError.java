package com.dnastack.discovery.registry.controller.exception;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationError implements Error {

    private String code;
    private String message;

}
