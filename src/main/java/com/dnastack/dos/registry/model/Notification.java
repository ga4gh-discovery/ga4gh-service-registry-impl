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
    private String code = null;
    private String message = null;
    private String uuid = null;
    private DateTime timestamp = null;
    private Map<String, Object> metadata = null;

}

