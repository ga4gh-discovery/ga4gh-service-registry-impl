package com.dnastack.dos.registry.exception;

import com.dnastack.dos.registry.model.Notification;

/**
 * Exception class indicating the intended data node resource does not belong to a user,
 * but the user tries to perform operations which can only be executed by its owner
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class DataNodeOwnershipException extends BaseException{

    public DataNodeOwnershipException(String message) {
        super(message, null);
    }

    public DataNodeOwnershipException(String message, Throwable cause, Notification... errors) {
        super(message, cause, errors);
    }
}
