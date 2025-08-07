package com.brunosong.transfer.system.transfer.service.helper.transfer;


import com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum.CurriculumSourceConverter;
import com.brunosong.transfer.system.transfer.service.valueobject.CurriculumBaseKey;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
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
public class TransferDataConvertHelper {

    private final CurriculumSourceConverter curriculumSourceConverter;
    private final ObjectMapper objectMapper;

    public List<byte[]> splitCurriculumJsonIntoChunks(SourceContentData sourceContentData) {

        List<byte[]> chunckDataList = new ArrayList<>();

        // 디비에 저장된 데이터를 가져온다.
        byte[] jsonData = sourceContentData.getJsonData();

        try {
            // 원본 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode curriculumNode = rootNode.path(CurriculumBaseKey.CURRICULUM.getKey());

            // 상위 데이터 grades & semester
            if (sourceContentData.getSourceType() == SourceType.CURRICULUM) {
                byte[] upperData = curriculumSourceConverter.buildCurriculumWithGradesAndSemesters(curriculumNode);
                chunckDataList.add(upperData);

                // 하위 데이터 전송
                List<byte[]> subjectDataList = curriculumSourceConverter.buildCurriculumWithSubject(curriculumNode);
                chunckDataList.addAll(subjectDataList);
            }

            return chunckDataList;
        } catch (Exception e) {
            log.error("Error converting curriculum source data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert curriculum source data", e);
        }

    }
}
