package com.dnastack.dos.registry.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@RestController
@RequestMapping("ga4gh/v1")
public class DataNodeController implements NodesApi{
    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> createNode(String authorization, Ga4ghDataNodeCreationRequestDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> deleteNode(String authorization) {
        return null;
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> getNodeById(String authorization) {
        return null;
    }

    @Override
    public ResponseEntity<Ga4ghDataNodesResponseDto> getNodes(String authorization, String name, String alias, String description, String providerInformation, String pageToken, Integer pageSize) {
        return null;
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> updateNode(String authorization, Ga4ghDataNodeUpdateRequestDto requestBody) {
        return null;
    }
}
