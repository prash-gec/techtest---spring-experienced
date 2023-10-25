package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataHeaderServiceImpl implements com.db.dataplatform.techtest.server.service.DataHeaderService {

    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveHeader(DataHeaderEntity entity) {
        dataHeaderRepository.save(entity);
    }

    @Override
    public List<DataHeaderEntity> findByBlockType(BlockTypeEnum blockType) {
        return dataHeaderRepository.findByBlocktype(blockType);
    }

    @Override
    public Optional<DataHeaderEntity> findByName(String name) {
        return dataHeaderRepository.findByName(name);
    }

}
