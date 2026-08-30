package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferChunkEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransferChunksJpaRepository extends JpaRepository<TransferChunkEntity, Long> {

    boolean existsByLog_IdAndChunkOffset(UUID transferLogId, int chunkOffset);
}
