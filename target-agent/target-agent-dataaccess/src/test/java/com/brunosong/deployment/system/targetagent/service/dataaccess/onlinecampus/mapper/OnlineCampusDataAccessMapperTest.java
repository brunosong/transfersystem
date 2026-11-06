package com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.mapper;

import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity.LessonEntity;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity.SemesterEntity;
import com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.entity.SubjectEntity;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnlineCampusDataAccessMapperTest {

    private final OnlineCampusDataAccessMapper mapper = new OnlineCampusDataAccessMapper();

    @Test
    @DisplayName("뼈대 내용은 학기까지만 엔티티로 옮기고 저장한 학년, 학기 수를 알려 준다")
    void skeleton() {
        ChunkContent.Semester first = new ChunkContent.Semester("S1", "1학기", "설명", List.of("SUB1", "SUB2"));
        ChunkContent.Semester second = new ChunkContent.Semester("S2", "2학기", null, List.of());
        ChunkContent.Skeleton skeleton = new ChunkContent.Skeleton("중등", "중등 과정",
                List.of(new ChunkContent.Grade("G1", "1학년", null, List.of(first, second))));

        CurriculumEntity entity = mapper.toCurriculum("CUR001", skeleton);

        assertEquals("CUR001", entity.getCurriculumId());
        assertEquals("중등", entity.getTitle());
        SemesterEntity semester = entity.getGradeEntities().get(0).getSemesterEntities().get(0);
        assertEquals("S1", semester.getSemesterId());
        assertTrue(semester.getSubjectEntities().isEmpty());

        entity.setId(1L); // 저장한 뒤에 부르는 메서드라 ID 가 있어야 한다
        assertEquals(new AppliedContent.Skeleton(1, 2, List.of()), mapper.toApplyOutcome(entity).applied());
    }

    @Test
    @DisplayName("과목 내용은 차시까지 엔티티로 옮기고 저장한 제목과 단원, 차시 수를 알려 준다")
    void subject() {
        ChunkContent.Lesson lesson1 = new ChunkContent.Lesson("L1", "차시 1", null, 1, 2);
        ChunkContent.Lesson lesson2 = new ChunkContent.Lesson("L2", "차시 2", null, 2, 3);
        ChunkContent.Subject subject = new ChunkContent.Subject("SUB1", "수학", "수학 학습", List.of(
                new ChunkContent.Unit("U1", "단원 1", null, List.of(lesson1, lesson2)),
                new ChunkContent.Unit("U2", "단원 2", null, List.of())));

        SubjectEntity entity = mapper.convertToSubject(subject, new SemesterEntity());

        LessonEntity lesson = entity.getUnitEntities().get(0).getLessonEntities().get(1);
        assertEquals("L2", lesson.getLessonId());
        assertEquals(2, lesson.getLessonNumber());
        assertEquals(3, lesson.getLearningLevel());

        entity.setId(1L);
        assertEquals(new AppliedContent.Subject("SUB1", "수학", 2, 2), mapper.toApplyOutcome(entity).applied());
    }
}
