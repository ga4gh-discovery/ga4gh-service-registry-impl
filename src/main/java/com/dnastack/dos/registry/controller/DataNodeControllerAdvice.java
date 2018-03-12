package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.BusinessValidationException;
import com.dnastack.dos.registry.exception.DataNodeNotFoundException;
import com.dnastack.dos.registry.exception.ServiceException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.util.*;

@ControllerAdvice(annotations = RestController.class)
public class DataNodeControllerAdvice {

    private static Logger logger = LoggerFactory.getLogger("DataNodeServiceLogger");

    @ExceptionHandler(DataNodeNotFoundException.class)
    ResponseEntity<ErrorDataResponseDto> handleDataNodeNotFoundException(
            DataNodeNotFoundException e) {
        String faultGuid = UUID.randomUUID().toString();
        final HttpStatus status = HttpStatus.NOT_FOUND;
        logError(e, faultGuid, status);

        return formResponse(status, formErrors("E1000", e.getMessage(), faultGuid, "VALIDATION", null));
    }

    @ExceptionHandler(BusinessValidationException.class)
    ResponseEntity<ErrorDataResponseDto> handleBusinessValidationException(
            BusinessValidationException e) {
        String faultGuid = UUID.randomUUID().toString();
        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        logError(e, faultGuid, status);

        return formResponse(status, formErrors("E8888", e.getMessage(), faultGuid, "BIZ_VALIDATION", null));
    }

    /**
     * This advice is meant to handle recognized exceptions thrown by frameworks (spring,
     * javax-validator etc.), which are meant to be a BAD_REQUEST exception
     */
    @ExceptionHandler({
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            ValidationException.class,
            HttpMessageNotReadableException.class
    })
    ResponseEntity<ErrorDataResponseDto> handleRecognizedFrameworkException(Exception e) {
        String faultGuid = UUID.randomUUID().toString();
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        logError(e, faultGuid, status);

        return formResponse(status, formErrors("E9998", e.getMessage(), faultGuid, "VALIDATION", null));
    }

    @ExceptionHandler({Exception.class, ServiceException.class})
    ResponseEntity<ErrorDataResponseDto> handleAnyException(Exception e) throws Exception {
        String faultGuid = UUID.randomUUID().toString();
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logError(e, faultGuid, status);

        return formResponse(status, formErrors("E9999", e.getMessage(), faultGuid, "INTERNAL", null));
    }

    /**
     * Logs the error message.
     *
     * @param e
     * @param faultGuid
     * @param status
     */
    private void logError(
            Exception e, String faultGuid, HttpStatus status) {
        logger.error(
                "Exception "
                        + e.getClass().getCanonicalName()
                        + " with faultId: "
                        + faultGuid
                        + " resolved to HTTP status: "
                        + status.toString(),
                e);
    }

    /**
     * Forms an error responses from the provided data
     *
     * @param status http status to respond with.
     * @param errors
     * @return a response for the client
     */
    private ResponseEntity<ErrorDataResponseDto> formResponse(HttpStatus status, List<ErrorDto> errors) {

        Assert.notNull(errors, "errors list cannot be null");

        ErrorDataResponseDto errorDataResponseDto = new ErrorDataResponseDto();
        errorDataResponseDto.setErrors(errors);

        return new ResponseEntity(errorDataResponseDto, status);
    }

    /**
     * Forms a list of error dtos
     *
     * @param code
     * @param message
     * @param faultGuid
     * @param source
     * @param cause
     * @return
     */
    public static List<ErrorDto> formErrors(String code,
                                            String message,
                                            String faultGuid,
                                            String source,
                                            Throwable cause) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(code);
        errorDto.setMessage(message);
        errorDto.setUuid(faultGuid);
        errorDto.setTimestamp(DateTime.now());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", source);
        if(cause!=null) {
            metadata.put("cause", cause);
        }
        errorDto.setMetadata(metadata);

        return Arrays.asList(errorDto);

    }

}
