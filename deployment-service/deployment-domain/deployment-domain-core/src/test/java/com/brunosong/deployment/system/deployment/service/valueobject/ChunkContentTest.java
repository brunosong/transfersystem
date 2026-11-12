package com.brunosong.deployment.system.deployment.service.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChunkContentTest {

    private static final Curriculum.Lesson LESSON = new Curriculum.Lesson("L1", "차시 1", null, 1, 3, null);
    private static final Curriculum.Subject KOREAN = new Curriculum.Subject("SUB1", "국어", null,
            List.of(new Curriculum.Unit("U1", "단원 1", null, null, List.of(LESSON))));
    private static final Curriculum.Subject MATH = new Curriculum.Subject("SUB2", "수학", null, List.of());
    private static final Curriculum.Subject SCIENCE = new Curriculum.Subject("SUB3", "과학", null, List.of());

    /** 1학년 1학기에 국어와 수학, 1학년 2학기에 과학이 있는 커리큘럼 */
    private static final Curriculum CURRICULUM = new Curriculum("CUR001", "중등", "중등 과정", List.of(
            new Curriculum.Grade("G1", "1학년", null, List.of(
                    new Curriculum.Semester("S1", "1학기", null, List.of(KOREAN, MATH)),
                    new Curriculum.Semester("S2", "2학기", null, List.of(SCIENCE))))));

    @Test
    @DisplayName("0번은 뼈대이고, 뼈대의 학기에는 과목 ID만 들어 있다")
    void firstChunkIsSkeletonWithSubjectIds() {
        List<ChunkContent> chunks = ChunkContent.split(CURRICULUM);

        ChunkContent.Skeleton skeleton = assertInstanceOf(ChunkContent.Skeleton.class, chunks.get(0));
        assertEquals(new ChunkContent.Skeleton("중등", "중등 과정", List.of(
                new ChunkContent.Grade("G1", "1학년", null, List.of(
                        new ChunkContent.Semester("S1", "1학기", null, List.of("SUB1", "SUB2")),
                        new ChunkContent.Semester("S2", "2학기", null, List.of("SUB3")))))), skeleton);
    }

    @Test
    @DisplayName("1번부터는 학년과 학기 순서대로 과목 하나에 청크 하나다")
    void subjectChunksFollowCurriculumOrder() {
        List<ChunkContent> chunks = ChunkContent.split(CURRICULUM);

        assertEquals(List.of(
                new ChunkContent.SubjectChunk("G1", "S1", KOREAN),
                new ChunkContent.SubjectChunk("G1", "S1", MATH),
                new ChunkContent.SubjectChunk("G1", "S2", SCIENCE)), chunks.subList(1, chunks.size()));
    }

    @Test
    @DisplayName("과목이 없는 커리큘럼은 뼈대 하나만 나온다")
    void curriculumWithoutSubjectsHasOnlySkeleton() {
        Curriculum empty = new Curriculum("CUR002", "빈 과정", null, List.of());

        List<ChunkContent> chunks = ChunkContent.split(empty);

        assertEquals(List.of(new ChunkContent.Skeleton("빈 과정", null, List.of())), chunks);
    }

    @Test
    @DisplayName("돌려준 청크 목록은 고칠 수 없다")
    void chunksCannotBeModified() {
        List<ChunkContent> chunks = ChunkContent.split(CURRICULUM);

        assertThrows(UnsupportedOperationException.class,
                () -> chunks.add(new ChunkContent.SubjectChunk("G1", "S1", MATH)));
    }
}
