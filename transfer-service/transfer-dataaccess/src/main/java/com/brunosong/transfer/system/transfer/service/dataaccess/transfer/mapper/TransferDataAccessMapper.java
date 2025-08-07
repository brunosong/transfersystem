package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper;

import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferOutboxEntity;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferDataAccessMapper {

    TransferOutboxEntity toTransferOutboxEntity(DataTransferOutboxMessage dataTransferOutboxMessage);
    DataTransferOutboxMessage toDataTransferOutboxMessage(TransferOutboxEntity transferOutboxEntity);
}
