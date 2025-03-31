package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.entity.Course;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumJpaRepository curriculumJpaRepository;

    @Override
    public void save(SourceContentData sourceContentData) {

        Document document = Document.parse(StandardCharsets.UTF_8.decode(sourceContentData.getJsonData()).toString());

        Curriculum curriculum = convertToCurriculum(document);

        curriculumJpaRepository.save(curriculum);
    }

    private Curriculum convertToCurriculum(Document document) {
        Document curriculumDoc = document.get("curriculum", Document.class);
        if (curriculumDoc == null) {
            throw new IllegalArgumentException("Invalid curriculum document: 'curriculum' field is missing");
        }

        Curriculum curriculum = new Curriculum();
        curriculum.setCurriculumId(curriculumDoc.getString("curriculumId"));
        curriculum.setTitle(curriculumDoc.getString("title"));
        curriculum.setDescription(curriculumDoc.getString("description"));

        List<Document> gradeDocs = curriculumDoc.getList("grade", Document.class);
        List<Grade> grades = new ArrayList<>();
        for (Document gradeDoc : gradeDocs) {
            grades.add(convertToGrade(gradeDoc, curriculum));
        }
        curriculum.setGrades(grades);

        return curriculum;
    }

    private Grade convertToGrade(Document gradeDoc, Curriculum curriculum) {
        Grade grade = new Grade();
        grade.setGradeId(gradeDoc.getString("id"));
        grade.setTitle(gradeDoc.getString("title"));
        grade.setDescription(gradeDoc.getString("description"));
        grade.setCurriculum(curriculum);

        List<Document> semesterDocs = gradeDoc.getList("semester", Document.class);
        List<Semester> semesters = new ArrayList<>();
        for (Document semesterDoc : semesterDocs) {
            semesters.add(convertToSemester(semesterDoc, grade));
        }
        grade.setSemesters(semesters);

        return grade;
    }

    private Semester convertToSemester(Document semesterDoc, Grade grade) {
        Semester semester = new Semester();
        semester.setSemesterId(semesterDoc.getString("id"));
        semester.setTitle(semesterDoc.getString("title"));
        semester.setDescription(semesterDoc.getString("description"));
        semester.setGrade(grade);

        List<Document> subjectDocs = semesterDoc.getList("subject", Document.class);
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

        List<Document> unitDocs = subjectDoc.getList("unit", Document.class);
        List<Unit> units = new ArrayList<>();
        for (Document unitDoc : unitDocs) {
            units.add(convertToUnit(unitDoc, subject));
        }
        subject.setUnits(units);

        return subject;
    }

    private Unit convertToUnit(Document unitDoc, Subject subject) {
        Unit unit = new Unit();
        unit.setUnitId(unitDoc.getString("id"));
        unit.setTitle(unitDoc.getString("title"));
        unit.setDescription(unitDoc.getString("description"));
        unit.setSubject(subject);

        List<Document> lessonDocs = unitDoc.getList("lesson", Document.class);
        List<Lesson> lessons = new ArrayList<>();
        for (Document lessonDoc : lessonDocs) {
            lessons.add(convertToLesson(lessonDoc, unit));
        }
        unit.setLessons(lessons);

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
