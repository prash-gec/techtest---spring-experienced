package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {

    public List<DataHeaderEntity> findByBlocktype(BlockTypeEnum blockType);

    // Assuming there will be one record for single name, although its not marked as primary key
    public Optional<DataHeaderEntity> findByName(String name);

}
