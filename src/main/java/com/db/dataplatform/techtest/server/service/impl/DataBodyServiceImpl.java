package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataBodyServiceImpl implements DataBodyService {

    private final DataStoreRepository dataStoreRepository;
    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveDataBody(DataBodyEntity dataBody) {
        dataStoreRepository.save(dataBody);
    }

    @Override
    public List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType) {
        return dataStoreRepository.findByDataHeaderEntityIn(dataHeaderRepository.findByBlocktype(blockType));
    }

    @Override
    public Optional<DataBodyEntity> getDataByBlockName(String blockName) {
        Optional<DataHeaderEntity> optionalHeader = dataHeaderRepository.findByName(blockName);
        if(optionalHeader.isPresent()){
            DataHeaderEntity header = optionalHeader.get();
            return dataStoreRepository.findByDataHeaderEntity(header);
        }
            return  Optional.empty();
    }
}
