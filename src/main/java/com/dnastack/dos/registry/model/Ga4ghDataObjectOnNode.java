package com.dnastack.dos.registry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ga4ghDataObjectOnNode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ga4ghDataObjectOnNode {
    private String nodeId = null;
    Ga4ghDataObject dataObject = null;
}

