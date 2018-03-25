package com.dnastack.dos.registry.exception;


import com.dnastack.dos.registry.model.Notification;

/**
 * This exception class will be thrown if there is a validation error from the page token
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class InvalidPageTokenException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidPageTokenException(String message) {
        super(message, null);
    }

    public InvalidPageTokenException(String message, Throwable cause, Notification... notifications) {
        super(message, cause, notifications);
    }
}
