package com.brunosong.transfer.system.deployment.service.helper.deployment;

import com.brunosong.transfer.system.deployment.service.valueobject.ChunkContent;

import com.brunosong.transfer.system.deployment.service.helper.deployment.curriculum.CurriculumSourceConverter;
import com.brunosong.transfer.system.deployment.service.valueobject.CurriculumBaseKey;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceType;
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
                sourceContentData.updateCurriculumId(curriculumSourceConverter.toCurriculumId(curriculumNode));
                chunks.add(curriculumSourceConverter.toSkeleton(curriculumNode));

                // 하위 데이터 배포
                chunks.addAll(curriculumSourceConverter.toSubjectChunks(curriculumNode));
            }

            return chunks;
        } catch (Exception e) {
            log.error("Error converting curriculum source data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert curriculum source data", e);
        }

    }
}
