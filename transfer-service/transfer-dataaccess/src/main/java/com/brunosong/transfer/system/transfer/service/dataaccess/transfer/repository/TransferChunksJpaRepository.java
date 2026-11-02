package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransferChunksJpaRepository extends JpaRepository<TransferChunkEntity, Long> {

    boolean existsByLog_IdAndChunkOffset(UUID transferLogId, int chunkOffset);

    List<TransferChunkEntity> findAllByLog_IdOrderByChunkOffsetAsc(UUID transferLogId);

    /**
     * 전송별·상태별 청크 수. 목록 화면의 진행률을 한 번의 쿼리로 구한다.
     * 상태는 "Success" 처럼 설명 문자열로 저장돼 있어 대문자로 맞춘다.
     */
    @Query("select c.log.id as transferId, upper(c.status) as status, count(c) as total "
            + "from TransferChunkEntity c where c.log.id in :ids group by c.log.id, upper(c.status)")
    List<ChunkStatusCount> countByStatus(@Param("ids") Collection<UUID> transferIds);

    interface ChunkStatusCount {
        UUID getTransferId();

        String getStatus();

        long getTotal();
    }
}
