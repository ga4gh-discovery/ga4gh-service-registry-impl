package com.dnastack.discovery.registry.controller.exception;

import com.dnastack.discovery.registry.service.ServiceInstanceExistsException;
import com.dnastack.discovery.registry.service.ServiceInstanceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.util.stream.Collectors.joining;

@Slf4j
@ControllerAdvice
public class RestControllerExceptionHandler {

    private ResponseEntity<Object> handleValidationException(BindingResult binding) {
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        String fieldErrors = binding.getFieldErrors().stream().map(FieldError::toString).collect(joining("; "));

        ErrorInfo responseBody = ErrorInfo.builder()
                .status(responseStatus.value())
                .title(responseStatus.getReasonPhrase())
                .detail(fieldErrors)
                .build();

        return ResponseEntity.status(responseStatus).body(responseBody);
    }

    private ValidationError buildValidationError(FieldError fieldError) {
        return ValidationError.builder()
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(RuntimeException ex) {
        HttpStatus responseStatus;
        if (ex instanceof IllegalArgumentException) {
            log.info("Assuming IllegalArgumentException is caused by user input", ex);
            responseStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ServiceInstanceNotFoundException) {
            responseStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ServiceInstanceExistsException) {
            responseStatus = HttpStatus.CONFLICT;
        } else {
            log.error("Unmapped exception", ex);
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorInfo.ErrorInfoBuilder errorBuilder = ErrorInfo.builder()
                .status(responseStatus.value())
                .title(responseStatus.getReasonPhrase())
                .detail(ex.getMessage());

        if (ex instanceof HasServiceInstanceId) {
            errorBuilder.serviceInstanceId(((HasServiceInstanceId) ex).getServiceInstanceId());
        }

        return ResponseEntity.status(responseStatus).body(errorBuilder.build());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationException(ex.getBindingResult());
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<Object> handleBindException(BindException ex) {
        return handleValidationException(ex.getBindingResult());
    }

}
