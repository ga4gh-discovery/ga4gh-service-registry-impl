package com.dnastack.dos.registry.downstream.passthru;

import com.dnastack.dos.registry.downstream.IDataClient;
import com.dnastack.dos.registry.downstream.dto.ChecksumRequestDto;
import com.dnastack.dos.registry.downstream.dto.ListDataObjectsRequestDto;
import com.dnastack.dos.registry.downstream.dto.ListDataObjectsResponseDto;
import com.dnastack.dos.registry.model.DataObjectPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This is the passthru implementation of {@link IDataClient}
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class PassThruDataClient implements IDataClient {

    private final RestTemplate objectsServiceRestTemplate;

    private String dataObjectsNodeURL;

    private DataObjectPage dataObjectPage;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PassThruDataClient(@Qualifier("objectsServiceRestTemplate") RestTemplate objectsServiceRestTemplate) {
        this.objectsServiceRestTemplate = objectsServiceRestTemplate;
    }

    @Override
    public ListDataObjectsResponseDto getDataObjects() {

        Assert.isTrue(!StringUtils.isEmpty(dataObjectsNodeURL), "dataObjectsNodeURL can not be null");
        Assert.notNull(dataObjectPage, "dataObjectPage can not be null");
        Assert.isTrue(dataObjectPage.getPageExecutionContext().getRemainingCountForPage()>0, "remaining count for page must be greater than 0");

        ResponseEntity<ListDataObjectsResponseDto> rs = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //map dataObjectPage to ListDataObjectsRequestDto
            ListDataObjectsRequestDto listDataObjectsRequestDto = new ListDataObjectsRequestDto();
            listDataObjectsRequestDto.setUrl(dataObjectPage.getDosUrl());
            listDataObjectsRequestDto.setAlias(dataObjectPage.getDosAlias());
            listDataObjectsRequestDto.setPageSize(dataObjectPage.getPageSize());
            listDataObjectsRequestDto.setPageToken(dataObjectPage.getPageExecutionContext().getCurrentNodePageToken());

            ChecksumRequestDto checksumRequestDto = new ChecksumRequestDto();
            checksumRequestDto.setChecksum(dataObjectPage.getDosChecksum());
            listDataObjectsRequestDto.setChecksum(checksumRequestDto);
            ObjectMapper mapperObj = new ObjectMapper();
            String jsonListDataObjectsRequestDto = mapperObj.writeValueAsString(listDataObjectsRequestDto);

            HttpEntity<String> entity = new HttpEntity<>(jsonListDataObjectsRequestDto, headers);
            rs = objectsServiceRestTemplate.exchange(dataObjectsNodeURL, HttpMethod.POST, entity, ListDataObjectsResponseDto.class);
        } catch (Exception e) {
            String faultId = UUID.randomUUID().toString();
            logger.error("faultId: " + faultId + " " + e.getMessage(), e);
            //silently ignore the downstream exception
            ListDataObjectsResponseDto dataObjectsResponseDto = new ListDataObjectsResponseDto();
            dataObjectsResponseDto.setDataObjects(new ArrayList<>());
            return dataObjectsResponseDto;
        }
        return rs.getBody();
    }
}
