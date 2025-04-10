package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurriculumSourceTypePublisher implements SourceTypePublisher {

    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @Override
    public void typePublisher(Transfer transfer) {

        byte[] jsonData = transfer.getSourceContentData().getJsonData();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 원본 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode curriculumNode = rootNode.path("curriculum");
            String curriculumId = curriculumNode.path("curriculumId").asText();

            // 상위 데이터 전송 grades & semester
            byte[] upperData = createUpperData(curriculumId, curriculumNode);
            transfer.getSourceContentData().updateJsonData(upperData);

            transferDataSendMessagePublisher.publish(transfer);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Grade와 Semester를 하나의 메시지로 묶음
    private byte[] createUpperData(String curriculumId, JsonNode curriculumNode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Grade와 Semester만 포함하는 상위 데이터 생성
        ObjectNode upperNode = objectMapper.createObjectNode();
        upperNode.put("curriculumId", curriculumId);

        ArrayNode gradesArray = objectMapper.createArrayNode();
        JsonNode grades = curriculumNode.path("grade");
        for (JsonNode grade : grades) {
            ObjectNode gradeNode = objectMapper.createObjectNode();
            gradeNode.put("id", grade.path("id").asText());
            gradeNode.put("title", grade.path("title").asText());
            gradeNode.put("description", grade.path("description").asText());

            ArrayNode semestersArray = objectMapper.createArrayNode();
            JsonNode semesters = grade.path("semester");
            for (JsonNode semester : semesters) {
                ObjectNode semesterNode = objectMapper.createObjectNode();
                semesterNode.put("id", semester.path("id").asText());
                semesterNode.put("title", semester.path("title").asText());
                semesterNode.put("description", semester.path("description").asText());
                semestersArray.add(semesterNode); // subject 제외
            }
            gradeNode.set("semester", semestersArray);
            gradesArray.add(gradeNode);
        }
        upperNode.set("grades", gradesArray);

        return objectMapper.writeValueAsBytes(upperNode);

    }

}
