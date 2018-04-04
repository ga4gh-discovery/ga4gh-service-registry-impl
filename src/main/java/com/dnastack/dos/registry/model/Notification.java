package com.dnastack.dos.registry.model;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * Notification
 */
@Data
@Builder
public class Notification {
    private String code;
    private String message;
    private String uuid;
    private DateTime timestamp;
    private Map<String, Object> metadata;

}

