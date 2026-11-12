package com.brunosong.deployment.system.deployment.service.helper.deployment;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.helper.deployment.curriculum.CurriculumSourceConverter;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import com.brunosong.deployment.system.deployment.service.valueobject.CurriculumBaseKey;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentDataConvertHelper {

    private final CurriculumSourceConverter curriculumSourceConverter;
    private final ObjectMapper objectMapper;

    /**
     * 꺼낸 원본 문서를 커리큘럼으로 바꾼다. 받은 문서는 건드리지 않고 새 커리큘럼을 돌려준다.
     * 지금 배포할 수 있는 원본 종류는 커리큘럼뿐이라 다른 종류는 거절한다.
     */
    public Curriculum toCurriculum(SourceDocument document) {
        if (document.sourceType() != SourceType.CURRICULUM) {
            throw new DeploymentDomainException("지원하지 않는 원본 종류: " + document.sourceType());
        }
        JsonNode curriculumNode = readTree(document).path(CurriculumBaseKey.CURRICULUM.getKey());
        return curriculumSourceConverter.toCurriculum(curriculumNode);
    }

    private JsonNode readTree(SourceDocument document) {
        try {
            return objectMapper.readTree(document.json());
        } catch (IOException e) {
            log.error("원본 문서를 JSON으로 읽지 못했다. sourceId={}", document.sourceId(), e);
            throw new IllegalStateException("원본 문서를 JSON으로 읽지 못했다: " + document.sourceId(), e);
        }
    }
}
