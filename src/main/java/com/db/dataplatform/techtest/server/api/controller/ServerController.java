package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.TechTestApplication;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());

        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);
        if(checksumPass)
            log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value = "/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getData(@Valid @PathVariable BlockTypeEnum blockType){
        log.info("fetching data for block type {}", blockType);
        return ResponseEntity.ok(server.getData(blockType));
    }

    @PatchMapping(value = "/update/{name}/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> patchData(@PathVariable String name, @Valid @PathVariable BlockTypeEnum blockType) throws IOException, NoSuchAlgorithmException {

        log.info("update received for block name : {} to blockType : {}", name, blockType);

        boolean status = server.updateDataEnvelope(name, blockType);
        if(status)
            log.info("Data Envelope Updated for block name : {} to blockType : {}", name, blockType);
        return ResponseEntity.ok(status);
    }

}
