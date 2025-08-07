package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransferOutboxJpaRepository extends JpaRepository<TransferOutboxEntity, UUID> {

}
