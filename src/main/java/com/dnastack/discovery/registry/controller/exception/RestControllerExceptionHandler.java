package com.dnastack.discovery.registry.controller.exception;

import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.service.ServiceNodeNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleException(RuntimeException ex) {
        HttpStatus responseStatus;
        if (ex instanceof IllegalArgumentException) {
            responseStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ServiceNodeNotFoundException) {
            responseStatus = HttpStatus.NOT_FOUND;
        } else {
            log.error("Unmapped exception", ex);
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorInfo responseBody = ErrorInfo.builder()
            .timestamp(ZonedDateTime.now())
            .status(responseStatus.value())
            .error(responseStatus.getReasonPhrase())
            .message(ex.getMessage())
            .build();

        return ResponseEntity.status(responseStatus)
            .body(responseBody);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationException(ex.getBindingResult());
    }

    @ExceptionHandler(value = { BindException.class })
    public ResponseEntity<Object> handleBindException(BindException ex) {
        return handleValidationException(ex.getBindingResult());
    }

    private ResponseEntity<Object> handleValidationException(BindingResult binding) {
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        List<ValidationFieldError> fieldErrors = binding
            .getFieldErrors()
            .stream()
            .map(fieldError -> ValidationFieldError.builder()
                .field(fieldError.getField())
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build())
            .collect(toList());

        ValidationError responseBody = ValidationError.builder()
            .timestamp(ZonedDateTime.now())
            .status(responseStatus.value())
            .validationErrors(fieldErrors)
            .build();

        return ResponseEntity.status(responseStatus)
            .body(responseBody);
    }

}
