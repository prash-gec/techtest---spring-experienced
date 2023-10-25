package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {
    public List<DataBodyEntity> findByDataHeaderEntityIn(List<DataHeaderEntity> headers);

    public Optional<DataBodyEntity> findByDataHeaderEntity(DataHeaderEntity header);
}
