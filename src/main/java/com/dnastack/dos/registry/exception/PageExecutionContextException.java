package com.dnastack.dos.registry.exception;


import com.dnastack.dos.registry.model.Notification;

/**
 * This exception class will be thrown if there is an error related to the current data node pool
 * in the process of searching data objects
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class PageExecutionContextException extends BaseException {

    private static final long serialVersionUID = 1L;

    public PageExecutionContextException(String message) {
        super(message, null);
    }

    public PageExecutionContextException(String message, Throwable cause, Notification... notifications) {
        super(message, cause, notifications);
    }
}
