package com.brunosong.transfer.system.transfer.service.valueobject;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SourceContentData {
    private int chunkOffset;
    private String sourceId;
    private SourceType sourceType;
    private DbType dbType;
    private byte[] jsonData;    // The original JSON data
    private List<ChunkContent> chunkDataList;  // 원본을 청크로 나눈 내용

    public void updateChunkDataList(List<ChunkContent> chunkDataList) {
        this.chunkDataList = chunkDataList;
    }
}
