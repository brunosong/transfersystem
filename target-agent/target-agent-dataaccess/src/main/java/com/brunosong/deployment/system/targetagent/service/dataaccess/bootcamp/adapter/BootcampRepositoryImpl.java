package com.brunosong.deployment.system.targetagent.service.dataaccess.bootcamp.adapter;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.BootcampRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 부트캠프의 납작한 표(program, term, course, chapter)에 쓴다(db/target/bootcamp).
 * 같은 청크가 두 번 와도 결과가 같아야 한다. 그래서 있으면 고치고 없으면 넣는다
 */
@Component
public class BootcampRepositoryImpl implements BootcampRepository {

    private final JdbcTemplate jdbcTemplate;

    public BootcampRepositoryImpl(@Qualifier("bootcampJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 이름을 적지 않으면 기본(Postgres) 트랜잭션 매니저를 탄다. 부트캠프 DB의 트랜잭션 매니저를 적어 준다
    @Override
    @Transactional(transactionManager = "bootcampTransactionManager")
    public AppliedContent saveOrUpdate(String curriculumId, ChunkContent content) {
        return switch (content) {
            case ChunkContent.Skeleton skeleton -> saveSkeleton(curriculumId, skeleton);
            case ChunkContent.SubjectChunk chunk -> saveSubject(chunk);
        };
    }

    /** 커리큘럼은 program 하나, 학년과 학기는 기수(term) 하나로 합친다. 예: "1학년 1학기" */
    private AppliedContent saveSkeleton(String curriculumId, ChunkContent.Skeleton skeleton) {
        upsert("UPDATE program SET title = ?, description = ? WHERE curriculum_id = ?",
                "INSERT INTO program (title, description, curriculum_id) VALUES (?, ?, ?)",
                skeleton.title(), skeleton.description(), curriculumId);

        int termOrder = 0;
        for (ChunkContent.Grade grade : skeleton.grades()) {
            for (ChunkContent.Semester semester : grade.semesters()) {
                termOrder++;
                upsert("UPDATE term SET curriculum_id = ?, title = ?, term_order = ? WHERE term_id = ?",
                        "INSERT INTO term (curriculum_id, title, term_order, term_id) VALUES (?, ?, ?, ?)",
                        curriculumId, grade.title() + " " + semester.title(), termOrder, semester.id());
            }
        }
        // 원본에서 지운 과목을 빼는 동작은 아직 없다(ROADMAP). 온라인캠퍼스와 같다
        return new AppliedContent.Skeleton(skeleton.grades().size(), termOrder, List.of());
    }

    /** 과목 하나를 course 로, 그 아래 차시를 순서대로 chapter 로. 차시는 지우고 다시 넣는다 */
    private AppliedContent saveSubject(ChunkContent.SubjectChunk chunk) {
        ChunkContent.Subject subject = chunk.subject();
        // 기수(term)가 없으면 외래 키에 걸려 실패한다. 뼈대가 먼저 반영돼야 한다(불변식 3)
        upsert("UPDATE course SET term_id = ?, title = ?, description = ? WHERE course_id = ?",
                "INSERT INTO course (term_id, title, description, course_id) VALUES (?, ?, ?, ?)",
                chunk.semesterId(), subject.title(), subject.description(), subject.id());

        jdbcTemplate.update("DELETE FROM chapter WHERE course_id = ?", subject.id());
        int chapterOrder = 0;
        for (ChunkContent.Unit unit : subject.units()) {
            for (ChunkContent.Lesson lesson : unit.lessons()) {
                chapterOrder++;
                jdbcTemplate.update("INSERT INTO chapter (chapter_id, course_id, unit_title, title, chapter_order, learning_level) "
                                + "VALUES (?, ?, ?, ?, ?, ?)",
                        lesson.id(), subject.id(), unit.title(), lesson.title(), chapterOrder, lesson.learningLevel());
            }
        }

        // 명령의 값이 아니라 저장한 뒤의 값을 돌려준다
        String savedTitle = jdbcTemplate.queryForObject("SELECT title FROM course WHERE course_id = ?", String.class, subject.id());
        Integer savedChapters = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chapter WHERE course_id = ?", Integer.class, subject.id());
        return new AppliedContent.Subject(subject.id(), savedTitle, subject.units().size(), savedChapters);
    }

    /** 있으면 고치고 없으면 넣는다. 두 SQL 은 같은 순서의 인자를 받는다(키가 맨 뒤) */
    private void upsert(String updateSql, String insertSql, Object... args) {
        if (jdbcTemplate.update(updateSql, args) == 0) {
            jdbcTemplate.update(insertSql, args);
        }
    }
}
