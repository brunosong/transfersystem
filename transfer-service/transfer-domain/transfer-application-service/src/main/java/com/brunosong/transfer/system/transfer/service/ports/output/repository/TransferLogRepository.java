package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.TransferLog;

public interface TransferLogRepository {

    TransferLog save(TransferLog transferLog);

}
