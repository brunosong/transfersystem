package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.CurriculumKey;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumJpaRepository curriculumJpaRepository;

    @Override
    public long save(SourceContentData sourceContentData) {

        Document document = Document.parse(StandardCharsets.UTF_8.decode(sourceContentData.getJsonData()).toString());
        Curriculum curriculum = convertToCurriculum(document);

        return curriculumJpaRepository.save(curriculum).getId();
    }

    private Curriculum convertToCurriculum(Document document) {
        Document curriculumDoc = document.get(CurriculumKey.CURRICULUM.getKey(), Document.class);
        if (curriculumDoc == null) {
            throw new IllegalArgumentException("Invalid curriculum document: 'curriculum' field is missing");
        }

        Curriculum curriculum = new Curriculum();
        curriculum.setCurriculumId(curriculumDoc.getString("curriculumId"));
        curriculum.setTitle(curriculumDoc.getString("title"));
        curriculum.setDescription(curriculumDoc.getString("description"));

        List<Document> gradeDocs = curriculumDoc.getList(CurriculumKey.GRADE.getKey(), Document.class, Collections.emptyList());
        if (!gradeDocs.isEmpty()) {
            List<Grade> grades = new ArrayList<>();
            for (Document gradeDoc : gradeDocs) {
                grades.add(convertToGrade(gradeDoc, curriculum));
            }
            curriculum.setGrades(grades);
        }
        return curriculum;
    }

    private Grade convertToGrade(Document gradeDoc, Curriculum curriculum) {
        Grade grade = new Grade();
        grade.setGradeId(gradeDoc.getString("id"));
        grade.setTitle(gradeDoc.getString("title"));
        grade.setDescription(gradeDoc.getString("description"));
        grade.setCurriculum(curriculum);

        List<Document> semesterDocs = gradeDoc.getList(CurriculumKey.SEMESTER.getKey(), Document.class, Collections.emptyList());
        if (!semesterDocs.isEmpty()) {
            List<Semester> semesters = new ArrayList<>();
            for (Document semesterDoc : semesterDocs) {
                semesters.add(convertToSemester(semesterDoc, grade));
            }
            grade.setSemesters(semesters);
        }
        return grade;
    }

    private Semester convertToSemester(Document semesterDoc, Grade grade) {
        Semester semester = new Semester();
        semester.setSemesterId(semesterDoc.getString("id"));
        semester.setTitle(semesterDoc.getString("title"));
        semester.setDescription(semesterDoc.getString("description"));
        semester.setGrade(grade);

        List<Document> subjectDocs = semesterDoc.getList(CurriculumKey.SUBJECT.getKey(), Document.class, Collections.emptyList());
        List<Subject> subjects = new ArrayList<>();

        for (Document subjectDoc : subjectDocs) {
            subjects.add(convertToSubject(subjectDoc, semester));
        }
        semester.setSubjects(subjects);

        return semester;
    }

    private Subject convertToSubject(Document subjectDoc, Semester semester) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectDoc.getString("id"));
        subject.setTitle(subjectDoc.getString("title"));
        subject.setDescription(subjectDoc.getString("description"));
        subject.setSemester(semester);

        List<Document> unitDocs = subjectDoc.getList(CurriculumKey.UNIT.getKey(), Document.class, Collections.emptyList());
        if (!unitDocs.isEmpty()) {
            List<Unit> units = new ArrayList<>();
            for (Document unitDoc : unitDocs) {
                units.add(convertToUnit(unitDoc, subject));
            }
            subject.setUnits(units);
        }
        return subject;
    }

    private Unit convertToUnit(Document unitDoc, Subject subject) {
        Unit unit = new Unit();
        unit.setUnitId(unitDoc.getString("id"));
        unit.setTitle(unitDoc.getString("title"));
        unit.setDescription(unitDoc.getString("description"));
        unit.setSubject(subject);

        List<Document> lessonDocs = unitDoc.getList(CurriculumKey.LESSON.getKey(), Document.class, Collections.emptyList());
        if (!lessonDocs.isEmpty()) {
            List<Lesson> lessons = new ArrayList<>();
            for (Document lessonDoc : lessonDocs) {
                lessons.add(convertToLesson(lessonDoc, unit));
            }
            unit.setLessons(lessons);
        }
        return unit;
    }

    private Lesson convertToLesson(Document lessonDoc, Unit unit) {
        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonDoc.getString("id"));
        lesson.setTitle(lessonDoc.getString("title"));
        lesson.setDescription(lessonDoc.getString("description"));
        lesson.setLessonNumber(lessonDoc.getInteger("lessonNumber"));
        lesson.setLearningLevel(lessonDoc.getInteger("learningLevel"));
        lesson.setUnit(unit);

        return lesson;
    }
}
