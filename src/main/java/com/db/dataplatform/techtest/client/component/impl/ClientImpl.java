package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.*;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    @Autowired
    public final RestTemplate restTemplate;

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        Boolean response = restTemplate.postForObject(URI_PUSHDATA, dataEnvelope, Boolean.class);
        log.info("received response {}", response);
    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("blockType", blockType);
        ResponseEntity<DataEnvelope[]> response = restTemplate.getForEntity(URI_GETDATA.expand(uriVariables), DataEnvelope[].class);
        if(response.hasBody())
            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        return Collections.emptyList();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("blockType", newBlockType);
        uriVariables.put("name", blockName);
        Boolean response = restTemplate.patchForObject(URI_PATCHDATA.expand(uriVariables), null, Boolean.class);
        if(response==null)
            return false;
        return response;
    }


}