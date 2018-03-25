package com.dnastack.dos.registry.exception;

import com.dnastack.dos.registry.model.Notification;

/**
 * Exception class indicating the intended data node resource cannot be found
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class DataNodeNotFoundException extends BaseException{

    public DataNodeNotFoundException(String message) {
        super(message, null);
    }

    public DataNodeNotFoundException(String message, Throwable cause, Notification... errors) {
        super(message, cause, errors);
    }
}
