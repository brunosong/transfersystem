package com.brunosong.transfer.system.transfer.service.valueobject;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SourceContentData {
    private int chunkOffset;
    private String sourceId;
    private SourceType sourceType;
    private DbType dbType;
    private byte[] jsonData;

    public void updateJsonData(byte[] jsonData) {
        this.jsonData = jsonData;
    }

    public void resetChunkOffset() {
        this.chunkOffset = 0;
    }
    public void plusChunkOffset() {
        this.chunkOffset++;
    }
}
