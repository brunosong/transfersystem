package com.brunosong.transfer.system.deployment.service.valueobject;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SourceContentData {
    private int chunkOffset;
    private String sourceId;    // 원본 저장소 안의 문서 ID. Mongo 면 _id 다
    private String curriculumId;    // 문서 안의 커리큘럼 ID. 대상에는 이 값이 간다
    private SourceType sourceType;
    private DbType dbType;
    private byte[] jsonData;    // The original JSON data
    private List<ChunkContent> chunkDataList;  // 원본을 청크로 나눈 내용

    public void updateCurriculumId(String curriculumId) {
        this.curriculumId = curriculumId;
    }

    public void updateChunkDataList(List<ChunkContent> chunkDataList) {
        this.chunkDataList = chunkDataList;
    }
}
