package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.deployment.service.valueobject.*;

import com.brunosong.deployment.system.deployment.service.helper.deployment.curriculum.CurriculumSourceConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentDataConvertHelper {

    private final CurriculumSourceConverter curriculumSourceConverter;
    private final ObjectMapper objectMapper;

    public List<ChunkContent> splitCurriculumJsonIntoChunks(SourceContentData sourceContentData) {

        List<ChunkContent> chunks = new ArrayList<>();

        // 디비에 저장된 데이터를 가져온다.
        byte[] jsonData = sourceContentData.getJsonData();

        try {
            // 원본 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode curriculumNode = rootNode.path(CurriculumBaseKey.CURRICULUM.getKey());

            // 상위 데이터 grades & semester
            if (sourceContentData.getSourceType() == SourceType.CURRICULUM) {
                Curriculum curriculum = curriculumSourceConverter.toCurriculum(curriculumNode);
                // 5단계에서 지운다. 지금은 뒤쪽 코드가 아직 SourceContentData에서 커리큘럼 ID를 읽는다
                sourceContentData.updateCurriculumId(curriculum.curriculumId());
                chunks.addAll(ChunkContent.split(curriculum));
            }

            return chunks;
        } catch (Exception e) {
            log.error("Error converting curriculum source data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert curriculum source data", e);
        }

    }
}
