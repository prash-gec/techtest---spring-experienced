package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.TechTestApplication;
import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;

    private final RestTemplate restTemplate;

    public static final String URI_DATALAKE = "http://localhost:8090/hadoopserver/pushbigdata";

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) throws NoSuchAlgorithmException, JsonProcessingException {

        if(isValidChecksum(envelope)){
            String data = new ObjectMapper().writeValueAsString(envelope);
            pushToDataLake(data);
            // Save to persistence.
            persist(envelope);
            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
            return true;
        }else{
            log.info("Invalid checksum provided, could not persist data name: {}", envelope.getDataHeader().getName());
            return false;
        }
    }

    @Override
    public boolean updateDataEnvelope(String name, BlockTypeEnum blockType) {
        Optional<DataBodyEntity> optionalBody = dataBodyServiceImpl.getDataByBlockName(name);
        if(optionalBody.isPresent()){
            DataBodyEntity body = optionalBody.get();
            body.getDataHeaderEntity().setBlocktype(blockType);
            log.info("Data updated successfully, data name: {}", name);
            return true;
        }else{
            log.info("Invalid blockname provided, could not update data name: {}", name);
            return false;
        }
    }

    @Override
    public List<DataEnvelope> getData(BlockTypeEnum blockType) {
        List<DataBodyEntity> dataBodyEntities = dataBodyServiceImpl.getDataByBlockType(blockType);
        List<DataEnvelope> dataEnvelopes = new ArrayList<>();
        for(DataBodyEntity ent: dataBodyEntities){
            DataBody body = modelMapper.map(ent, DataBody.class);
            DataHeader header = modelMapper.map(ent.getDataHeaderEntity(), DataHeader.class);
            dataEnvelopes.add(new DataEnvelope(header, body));
        }
        return dataEnvelopes;
    }

    private boolean isValidChecksum(DataEnvelope envelope) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(envelope.getDataBody().getDataBody().getBytes());
        byte[] digest = md.digest();
        String inputChecksum = DatatypeConverter
                .printHexBinary(digest);
        return inputChecksum.equalsIgnoreCase(TechTestApplication.MD5_CHECKSUM);
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }


    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    @Retryable(maxAttempts=5, value = RuntimeException.class,
            backoff = @Backoff(delay = 15000, multiplier = 2))
    private void pushToDataLake(String data){
        restTemplate.postForObject(URI_DATALAKE, data, ResponseEntity.class);
    }


}
