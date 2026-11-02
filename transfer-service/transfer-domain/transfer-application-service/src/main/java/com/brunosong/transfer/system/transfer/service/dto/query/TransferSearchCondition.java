package com.brunosong.transfer.system.transfer.service.dto.query;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;

/**
 * 전송 목록 조회 조건. status 가 null 이면 전체를 본다.
 */
public record TransferSearchCondition(TransferStatus status, int page, int size) {

    public static final int MAX_SIZE = 100;

    public TransferSearchCondition {
        if (page < 0) page = 0;
        if (size <= 0 || size > MAX_SIZE) size = 20;
    }
}
