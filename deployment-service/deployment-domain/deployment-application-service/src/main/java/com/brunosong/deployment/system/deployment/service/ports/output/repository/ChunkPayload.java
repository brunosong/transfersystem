package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;

/** 보관해 둔 청크 내용 하나. 번호와 내용을 함께 돌려준다 */
public record ChunkPayload(int chunkNo, ChunkContent content) {
}
