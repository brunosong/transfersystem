package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkPayloadEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.ChunkPayloadJpaRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChunkPayloadRepositoryImpl implements ChunkPayloadRepository {

    private final ChunkPayloadJpaRepository chunkPayloadJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveAll(UUID deploymentId, List<ChunkContent> chunks) {
        List<ChunkPayloadEntity> entities = new ArrayList<>();
        for (int chunkNo = 0; chunkNo < chunks.size(); chunkNo++) {
            entities.add(new ChunkPayloadEntity(new ChunkEntityId(deploymentId, chunkNo), toJson(chunks.get(chunkNo))));
        }
        chunkPayloadJpaRepository.saveAll(entities);
    }

    @Override
    public List<ChunkPayload> findAll(UUID deploymentId, Collection<Integer> chunkNos) {
        return chunkPayloadJpaRepository
                .findAllById_DeploymentIdAndId_ChunkNoInOrderById_ChunkNoAsc(deploymentId, chunkNos)
                .stream()
                .map(entity -> new ChunkPayload(entity.getId().getChunkNo(), fromJson(entity.getContent())))
                .toList();


    }

    /**
     * 저장하는 JSON 모양. 종류마다 칸을 나눈다.
     * ChunkContent(sealed interface)를 그대로 쓰면 어느 종류인지 적는 Jackson 표시를 도메인에 달아야 한다.
     */
    record ContentJson(ChunkContent.Skeleton skeleton, ChunkContent.SubjectChunk subjectChunk) {

        static ContentJson of(ChunkContent content) {
            return switch (content) {
                case ChunkContent.Skeleton skeleton -> new ContentJson(skeleton, null);
                case ChunkContent.SubjectChunk subjectChunk -> new ContentJson(null, subjectChunk);
            };
        }

        ChunkContent toContent() {
            return skeleton != null ? skeleton : subjectChunk;
        }
    }

    private String toJson(ChunkContent content) {
        try {
            return objectMapper.writeValueAsString(ContentJson.of(content));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("청크 내용을 JSON 으로 바꾸지 못했다", e);
        }
    }

    private ChunkContent fromJson(String json) {
        try {
            return objectMapper.readValue(json, ContentJson.class).toContent();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("저장된 청크 내용을 읽지 못했다", e);
        }
    }
}
