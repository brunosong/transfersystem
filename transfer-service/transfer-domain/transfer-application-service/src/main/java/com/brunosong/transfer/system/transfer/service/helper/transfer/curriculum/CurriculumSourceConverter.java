package com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum;

import com.brunosong.transfer.system.transfer.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.transfer.service.valueobject.CurriculumBaseKey;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 저작 문서를 청크 내용으로 옮긴다. 저작 문서의 필드 이름을 아는 곳은 여기뿐이다.
 * 필드가 빠진 문서는 대상에 나가기 전에, 접수할 때 실패한다.
 */
@Component
public class CurriculumSourceConverter {

    public ChunkContent.Skeleton toSkeleton(JsonNode curriculumNode) {
        List<ChunkContent.Grade> grades = new ArrayList<>();
        for (JsonNode grade : curriculumNode.path(CurriculumBaseKey.LEVEL1.getKey())) {
            List<ChunkContent.Semester> semesters = new ArrayList<>();
            for (JsonNode semester : grade.path(CurriculumBaseKey.LEVEL2.getKey())) {
                List<String> subjectIds = new ArrayList<>();
                for (JsonNode subject : semester.path(CurriculumBaseKey.LEVEL3.getKey())) {
                    subjectIds.add(requiredText(subject, "id"));
                }
                semesters.add(new ChunkContent.Semester(requiredText(semester, "id"), requiredText(semester, "title"),
                        optionalText(semester, "description"), subjectIds));
            }
            grades.add(new ChunkContent.Grade(requiredText(grade, "id"), requiredText(grade, "title"),
                    optionalText(grade, "description"), semesters));
        }
        return new ChunkContent.Skeleton(requiredText(curriculumNode, "title"),
                optionalText(curriculumNode, "description"), grades);
    }

    public List<ChunkContent.SubjectChunk> toSubjectChunks(JsonNode curriculumNode) {
        List<ChunkContent.SubjectChunk> chunks = new ArrayList<>();
        for (JsonNode grade : curriculumNode.path(CurriculumBaseKey.LEVEL1.getKey())) {
            String gradeId = requiredText(grade, "id");
            for (JsonNode semester : grade.path(CurriculumBaseKey.LEVEL2.getKey())) {
                String semesterId = requiredText(semester, "id");
                for (JsonNode subject : semester.path(CurriculumBaseKey.LEVEL3.getKey())) {
                    chunks.add(new ChunkContent.SubjectChunk(gradeId, semesterId, toSubject(subject)));
                }
            }
        }
        return chunks;
    }

    private ChunkContent.Subject toSubject(JsonNode subject) {
        List<ChunkContent.Unit> units = new ArrayList<>();
        for (JsonNode unit : subject.path(CurriculumBaseKey.LEVEL4.getKey())) {
            units.add(toUnit(unit));
        }
        return new ChunkContent.Subject(requiredText(subject, "id"), requiredText(subject, "title"),
                optionalText(subject, "description"), units);
    }

    private ChunkContent.Unit toUnit(JsonNode unit) {
        List<ChunkContent.Lesson> lessons = new ArrayList<>();
        for (JsonNode lesson : unit.path(CurriculumBaseKey.LEVEL5.getKey())) {
            lessons.add(new ChunkContent.Lesson(requiredText(lesson, "id"), requiredText(lesson, "title"),
                    optionalText(lesson, "description"), requiredInt(lesson, "lessonNumber"),
                    requiredInt(lesson, "learningLevel")));
        }
        return new ChunkContent.Unit(requiredText(unit, "id"), requiredText(unit, "title"),
                optionalText(unit, "description"), lessons);
    }

    private String requiredText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isTextual()) {
            throw new IllegalArgumentException("저작 문서에 문자열 필드 '" + field + "' 가 없습니다: " + describe(node));
        }
        return value.asText();
    }

    private String optionalText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isTextual() ? value.asText() : null;
    }

    private int requiredInt(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isInt()) {
            throw new IllegalArgumentException("저작 문서에 정수 필드 '" + field + "' 가 없습니다: " + describe(node));
        }
        return value.asInt();
    }

    // 큰 문서 전체를 오류 메시지에 넣지 않는다
    private String describe(JsonNode node) {
        return "id=" + node.path("id").asText("(없음)");
    }
}
