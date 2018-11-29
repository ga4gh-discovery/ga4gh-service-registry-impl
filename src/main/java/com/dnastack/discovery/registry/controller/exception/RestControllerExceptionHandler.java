package com.dnastack.discovery.registry.controller.exception;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.dnastack.discovery.registry.service.ServiceNodeNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

        List<Error> errors = ex.getMessage() != null && !ex.getMessage().isEmpty()
            ? singletonList(buildApplicationError(ex))
            : emptyList();

        ErrorInfo responseBody = ErrorInfo.builder()
            .timestamp(ZonedDateTime.now())
            .code(responseStatus.value())
            .message(responseStatus.getReasonPhrase())
            .errors(errors)
            .build();

        return ResponseEntity.status(responseStatus)
            .body(responseBody);
    }

    private ApplicationError buildApplicationError(RuntimeException ex) {
        return ApplicationError.builder()
            .message(ex.getMessage())
            .build();
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

        List<Error> fieldErrors = binding
            .getFieldErrors()
            .stream()
            .map(this::buildValidationError)
            .collect(toList());

        ErrorInfo responseBody = ErrorInfo.builder()
            .timestamp(ZonedDateTime.now())
            .code(responseStatus.value())
            .message(responseStatus.getReasonPhrase())
            .errors(fieldErrors)
            .build();

        return ResponseEntity.status(responseStatus)
            .body(responseBody);
    }

    private ValidationError buildValidationError(FieldError fieldError) {
        return ValidationError.builder()
            .code(fieldError.getCode())
            .message(fieldError.getDefaultMessage())
            .field(fieldError.getField())
            .rejectedValue(fieldError.getRejectedValue())
            .build();
    }

}
