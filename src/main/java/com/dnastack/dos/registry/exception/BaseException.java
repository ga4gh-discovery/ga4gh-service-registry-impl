package com.dnastack.dos.registry.exception;


import com.dnastack.dos.registry.model.Notification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    private final List<Notification> errors;


    public BaseException(String message, Throwable cause, Notification... errors) {

        super(message, cause);

        if(errors !=null){
            this.errors = Arrays.stream(errors).collect(Collectors.toList());
        }else{
            this.errors = Collections.emptyList();
        }

    }

    public List<Notification> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
