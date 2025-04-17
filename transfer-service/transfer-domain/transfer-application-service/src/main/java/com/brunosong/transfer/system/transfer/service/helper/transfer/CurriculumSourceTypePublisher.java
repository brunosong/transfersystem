package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.CurriculumBaseKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurriculumSourceTypePublisher implements SourceTypePublisher {

    private final ObjectMapper objectMapper;
    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @Override
    public void typePublisher(Transfer transfer) {

        byte[] jsonData = transfer.getSourceContentData().getJsonData();

        try {
            // 원본 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode curriculumNode = rootNode.path(CurriculumBaseKey.CURRICULUM.getKey());

            // 상위 데이터 전송 grades & semester
            createUpperDataAndPublish(transfer, curriculumNode);

            Thread.sleep(1000);
            // 하위 데이터 전송
            extractAndPublishSubjects(transfer,curriculumNode);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Grade와 Semester를 하나의 메시지로 묶음
    private void createUpperDataAndPublish(Transfer transfer, JsonNode curriculumNode) throws Exception {
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

        byte[] upperData = objectMapper.writeValueAsBytes(rootNode);
        
        // 시작 청크는 0
        transfer.getSourceContentData().resetChunkOffset();
        transfer.getSourceContentData().updateJsonData(upperData);
        transferDataSendMessagePublisher.publish(transfer);

    }

    private void extractAndPublishSubjects(Transfer transfer, JsonNode curriculumNode) throws Exception {
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

                        // 데이터 카운트 추가
                        transfer.getSourceContentData().plusChunkOffset();
                        transfer.getSourceContentData().updateJsonData(subjectData);
                        //transferDataSendMessagePublisher.publish(transfer);
                    }
                }
            }
        }
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
