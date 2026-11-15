package com.brunosong.deployment.system.deployment.service.dto.query;

import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurriculumTreeViewTest {

    @Test
    @DisplayName("트리는 단계별 개수와 청크 수(뼈대 1 + 과목 수)를 센다")
    void countsEveryLevelAndChunks() {
        CurriculumTreeView tree = CurriculumTreeView.from(curriculum());

        assertEquals(new CurriculumTreeView.Counts(1, 2, 3, 2, 3), tree.counts());
        assertEquals(4, tree.chunkCount());
        CurriculumTreeView.SubjectNode math = tree.grades().get(0).semesters().get(0).subjects().get(0);
        assertEquals(new CurriculumTreeView.SubjectNode("S1", "수학", 2, 3), math);
    }

    @Test
    @DisplayName("목록 한 줄은 커리큘럼의 과목 수를 모든 학기에 걸쳐 센다")
    void summaryCountsSubjectsAcrossSemesters() {
        assertEquals(new CurriculumSummaryView("CUR01", "초등 과정", 3), CurriculumSummaryView.from(curriculum()));
    }

    /** 학년 하나, 학기 둘. 1학기에 수학(단원 둘, 차시 셋)과 국어, 2학기에 과학 */
    private static Curriculum curriculum() {
        Curriculum.Lesson l1 = new Curriculum.Lesson("L1", "덧셈", null, 1, 1, null);
        Curriculum.Lesson l2 = new Curriculum.Lesson("L2", "뺄셈", null, 2, 1, null);
        Curriculum.Lesson l3 = new Curriculum.Lesson("L3", "도형", null, 3, 1, null);
        Curriculum.Unit u1 = new Curriculum.Unit("U1", "수와 연산", null, null, List.of(l1, l2));
        Curriculum.Unit u2 = new Curriculum.Unit("U2", "도형", null, null, List.of(l3));
        Curriculum.Subject math = new Curriculum.Subject("S1", "수학", null, List.of(u1, u2));
        Curriculum.Subject korean = new Curriculum.Subject("S2", "국어", null, List.of());
        Curriculum.Subject science = new Curriculum.Subject("S3", "과학", null, List.of());
        Curriculum.Semester first = new Curriculum.Semester("SEM1", "1학기", null, List.of(math, korean));
        Curriculum.Semester second = new Curriculum.Semester("SEM2", "2학기", null, List.of(science));
        Curriculum.Grade grade = new Curriculum.Grade("G1", "1학년", null, List.of(first, second));
        return new Curriculum("CUR01", "초등 과정", null, List.of(grade));
    }
}
