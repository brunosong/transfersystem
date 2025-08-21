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
    private List<byte[]> chunkDataList;  // List of byte arrays representing chunks of the JSON data

    public void updateChunkDataList(List<byte[]> chunkDataList) {
        this.chunkDataList = chunkDataList;
    }
}
