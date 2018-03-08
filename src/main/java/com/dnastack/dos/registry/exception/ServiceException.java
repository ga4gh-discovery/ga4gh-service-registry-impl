package com.dnastack.dos.registry.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This exception models a generic service exception
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String id = UUID.randomUUID().toString();
    private final Category category;
    private final String faultCode;
    private final Set<String> problems = new LinkedHashSet();
    private final long timestamp = Instant.now().getEpochSecond();

    public ServiceException() {
        this.category = Category.INTERNAL;
        this.faultCode = null;
    }

    public ServiceException(String message) {
        super(message);
        this.category = Category.INTERNAL;
        this.faultCode = null;
    }

    public ServiceException(Throwable cause) {
        super(cause);
        this.category = Category.INTERNAL;
        this.faultCode = null;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.category = Category.INTERNAL;
        this.faultCode = null;
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.category = Category.INTERNAL;
        this.faultCode = null;
    }

    public ServiceException(String message, Throwable cause, Category category, String faultCode, Collection<String> problems) {
        super(message, cause);
        this.category = category != null?category:Category.INTERNAL;
        this.faultCode = faultCode;
        this.problems.addAll(problems);
    }

    public Set<String> problems() {
        return Collections.unmodifiableSet(this.problems);
    }

    public String getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getFaultCode() {
        return this.faultCode;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public static class Builder {
        private Category category;
        private String faultCode;
        private List<String> problems = new ArrayList();
        private String message;
        private Throwable cause;

        public Builder() {
        }

        public ServiceException.Builder category(Category category) {
            this.category = category;
            return this;
        }

        public ServiceException.Builder faultCode(String faultCode) {
            this.faultCode = faultCode;
            return this;
        }

        public ServiceException.Builder problem(String problem) {
            this.problems.add(problem);
            return this;
        }

        public ServiceException.Builder allProblems(List<String> problems) {
            this.problems.addAll(problems);
            return this;
        }

        public ServiceException.Builder message(String message) {
            this.message = message;
            return this;
        }

        public ServiceException.Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public ServiceException build() {
            return new ServiceException(this.message, this.cause, this.category, this.faultCode, this.problems);
        }
    }

    private static enum Category {
        INTERNAL,
        BACKEND,
        SECURITY,
        VALIDATION;

        private Category() {
        }
    }
}
