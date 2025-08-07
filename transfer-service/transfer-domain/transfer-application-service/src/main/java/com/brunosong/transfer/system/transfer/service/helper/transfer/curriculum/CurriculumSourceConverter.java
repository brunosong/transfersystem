package com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum;


import com.brunosong.transfer.system.transfer.service.valueobject.CurriculumBaseKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CurriculumSourceConverter {

    private final ObjectMapper objectMapper;

    // Grade와 Semester를 하나의 메시지로 묶음
    public byte[] buildCurriculumWithGradesAndSemesters(JsonNode curriculumNode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. 최상위 ObjectNode 생성
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode newCurriculumNode = objectMapper.createObjectNode();
        rootNode.set(CurriculumBaseKey.CURRICULUM.getKey(), newCurriculumNode);

        // 2. curriculum 필드 추가
        copyTextNode(curriculumNode, newCurriculumNode);

        // 3. grades 배열 생성
        ArrayNode gradesArray = objectMapper.createArrayNode();
        JsonNode grades = curriculumNode.path(CurriculumBaseKey.LEVEL1.getKey());
        for (JsonNode grade : grades) {
            ObjectNode gradeNode = objectMapper.createObjectNode();
            copyTextNode(grade, gradeNode);

            // 4. semester 배열 생성
            ArrayNode semestersArray = objectMapper.createArrayNode();
            JsonNode semesters = grade.path(CurriculumBaseKey.LEVEL2.getKey());
            for (JsonNode semester : semesters) {
                ObjectNode semesterNode = objectMapper.createObjectNode();
                copyTextNode(semester, semesterNode);
                semestersArray.add(semesterNode); // subject 제외
            }
            gradeNode.set(CurriculumBaseKey.LEVEL2.getKey(), semestersArray);
            gradesArray.add(gradeNode);
        }
        newCurriculumNode.set(CurriculumBaseKey.LEVEL1.getKey(), gradesArray);

        return objectMapper.writeValueAsBytes(rootNode);
    }

    public List<byte[]> buildCurriculumWithSubject(JsonNode curriculumNode) throws Exception {

        List<byte[]> subjectDataList = new ArrayList<>();

        JsonNode grades = curriculumNode.path(CurriculumBaseKey.LEVEL1.getKey());

        String curriculumId = curriculumNode.path("curriculumId").asText();
        for (JsonNode grade : grades) {
            String gradeId = grade.path("id").asText();
            JsonNode semesters = grade.path(CurriculumBaseKey.LEVEL2.getKey());

            for (JsonNode semester : semesters) {
                String semesterId = semester.path("id").asText();
                JsonNode subjects = semester.path(CurriculumBaseKey.LEVEL3.getKey());
                if (subjects.isArray()) {
                    for (JsonNode subject : subjects) {
                        // subject 단위로 새 JSON 생성
                        ObjectNode subjectNode = createSubjectNode(curriculumId, gradeId, semesterId, subject);

                        // Transfer 객체 업데이트 및 전송
                        byte[] subjectData = objectMapper.writeValueAsBytes(subjectNode);
                        subjectDataList.add(subjectData);
                    }
                }
            }
        }

        return subjectDataList;
    }

    private ObjectNode createSubjectNode(String curriculumId, String gradeId, String semesterId, JsonNode subject) {
        ObjectNode subjectNode = objectMapper.createObjectNode();
        subjectNode.put("curriculumId", curriculumId);
        subjectNode.put("gradeId", gradeId);
        subjectNode.put("semesterId", semesterId);
        subjectNode.set("subject", subject);

        return subjectNode;
    }

    public void copyTextNode(JsonNode objectNode, ObjectNode newObjectNode) {
        objectNode.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode valueNode = entry.getValue();

            // 텍스트 값만 추가
            if (valueNode.isTextual()) {
                newObjectNode.put(fieldName, valueNode.asText());
            }
        });
    }
}
