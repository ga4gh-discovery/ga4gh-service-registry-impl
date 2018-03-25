package com.dnastack.dos.registry.exception;


import com.dnastack.dos.registry.model.Notification;

/**
 * Exception class indicating the request was not semantically correct.
 * And generally, this exception should be used related to errors with a
 * business logic validation
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class BusinessValidationException extends BaseException{

    public BusinessValidationException(String message) {
        super(message, null);
    }

    public BusinessValidationException(String message, Throwable cause, Notification... errors) {
        super(message, cause, errors);
    }
}
