package com.brunosong.transfer.system.transfer.service.dto.create;

import java.util.UUID;

/**
 * 전송 요청을 받아 아웃박스에 저장까지 마친 결과. 실제 반영은 비동기로 진행되므로
 * 클라이언트는 transferId 로 진행 상태를 조회한다.
 */
public record CreateTransferResponse(UUID transferId, int chunkCount) {
}
