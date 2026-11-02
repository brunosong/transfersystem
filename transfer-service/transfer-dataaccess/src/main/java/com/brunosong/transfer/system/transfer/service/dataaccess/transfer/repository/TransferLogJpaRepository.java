package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferLogJpaRepository extends JpaRepository<TransferLogEntity, UUID> {

    Page<TransferLogEntity> findAllByTransferStatus(TransferStatus transferStatus, Pageable pageable);

    @Query("select t.transferStatus as status, count(t) as total from TransferLogEntity t group by t.transferStatus")
    List<StatusCount> countByStatus();

    interface StatusCount {
        TransferStatus getStatus();

        long getTotal();
    }
}
