package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.TransferId;

public record TransferLogResult(TransferId transferId, String logMessage) {
}
