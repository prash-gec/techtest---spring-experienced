package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.util.List;
import java.util.Optional;

public interface DataHeaderService {
    void saveHeader(DataHeaderEntity entity);
    List<DataHeaderEntity> findByBlockType(BlockTypeEnum blockType);

    Optional<DataHeaderEntity> findByName(String name);
}
