package com.db.dataplatform.techtest.server.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class HadoopClient {

    public static final String URI_DATALAKE = "http://localhost:8090/hadoopserver/pushbigdata";

    @Retryable(maxAttempts=5, value = RuntimeException.class,
            backoff = @Backoff(delay = 2))
    public void pushToDataLake(String data){
        try{
            new RestTemplate().postForObject(URI_DATALAKE, data, ResponseEntity.class);
        }catch (RuntimeException re){
            log.error("Exception {} from hadoop backend retrying..", re.getMessage());
            throw new RuntimeException();
        }
    }
}
