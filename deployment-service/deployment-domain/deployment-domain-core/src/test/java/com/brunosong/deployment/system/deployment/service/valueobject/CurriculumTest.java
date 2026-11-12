package com.brunosong.deployment.system.deployment.service.valueobject;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurriculumTest {

    @Test
    @DisplayName("커리큘럼 ID가 비어 있으면 만들 수 없다")
    void blankCurriculumIdIsRejected() {
        assertThrows(DeploymentDomainException.class,
                () -> new Curriculum(" ", "중등", null, List.of()));
        assertThrows(DeploymentDomainException.class,
                () -> new Curriculum(null, "중등", null, List.of()));
    }

    @Test
    @DisplayName("커리큘럼 제목이 비어 있으면 만들 수 없다")
    void blankTitleIsRejected() {
        assertThrows(DeploymentDomainException.class,
                () -> new Curriculum("CUR001", "", null, List.of()));
    }

    @Test
    @DisplayName("만들 때 넘긴 목록을 나중에 고쳐도 커리큘럼은 바뀌지 않는다")
    void gradesAreCopied() {
        List<Curriculum.Grade> grades = new ArrayList<>();
        grades.add(new Curriculum.Grade("G1", "1학년", null, List.of()));

        Curriculum curriculum = new Curriculum("CUR001", "중등", null, grades);
        grades.add(new Curriculum.Grade("G2", "2학년", null, List.of()));

        assertEquals(1, curriculum.grades().size());
        assertThrows(UnsupportedOperationException.class,
                () -> curriculum.grades().add(new Curriculum.Grade("G3", "3학년", null, List.of())));
    }

    @Test
    @DisplayName("단원과 차시의 attributes가 null이면 빈 JSON 객체가 된다")
    void nullAttributesBecomeEmptyObject() {
        Curriculum.Lesson lesson = new Curriculum.Lesson("L1", "차시 1", null, 1, 3, null);
        Curriculum.Unit unit = new Curriculum.Unit("U1", "단원 1", null, null, List.of(lesson));

        assertEquals("{}", lesson.attributes());
        assertEquals("{}", unit.attributes());
    }

}