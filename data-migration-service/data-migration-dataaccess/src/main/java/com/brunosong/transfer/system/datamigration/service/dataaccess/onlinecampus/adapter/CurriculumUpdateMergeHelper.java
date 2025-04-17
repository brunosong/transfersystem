package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.GradeEntity;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.SemesterEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurriculumUpdateMergeHelper {

    public CurriculumEntity curriculumUpdate(CurriculumEntity existingCurriculumEntity,CurriculumEntity newCurriculumEntity) {

        existingCurriculumEntity.setDescription(newCurriculumEntity.getDescription());
        existingCurriculumEntity.setTitle(newCurriculumEntity.getTitle());

        List<GradeEntity> existingGrades = existingCurriculumEntity.getGradeEntities();
        List<GradeEntity> newGrades = newCurriculumEntity.getGradeEntities();

        // 기존 GradeEntity 업데이트
        Map<String, GradeEntity> existingGradeMap = existingGrades.stream()
                .collect(Collectors.toMap(GradeEntity::getGradeId, grade -> grade));

        for (GradeEntity newGrade : newGrades) {
            GradeEntity existingGrade = existingGradeMap.get(newGrade.getGradeId());
            if (existingGrade != null) {
                // 기존 GradeEntity 업데이트
                existingGrade.setTitle(newGrade.getTitle());
                existingGrade.setDescription(newGrade.getDescription());

                mergeSemester(existingGrade, newGrade);
            } else {
                // 새 GradeEntity 추가
                newGrade.setCurriculumEntity(existingCurriculumEntity);
                existingGrades.add(newGrade);
            }
        }

        // 삭제된 GradeEntity 제거
        existingGrades.removeIf(grade -> !newGrades.stream()
                .anyMatch(newGrade -> newGrade.getGradeId().equals(grade.getGradeId())));

        return existingCurriculumEntity;
    }

    public GradeEntity mergeSemester(GradeEntity existingGrade, GradeEntity newGrade) {

        List<SemesterEntity> semesterEntities = existingGrade.getSemesterEntities();
        List<SemesterEntity> newSemesters = newGrade.getSemesterEntities();

        Map<String, SemesterEntity> existingSemesterMap = semesterEntities.stream()
                .collect(Collectors.toMap(SemesterEntity::getSemesterId, semester -> semester));

        for (SemesterEntity newSemester : newSemesters) {
            SemesterEntity existingSemester = existingSemesterMap.get(newSemester.getSemesterId());

            if (existingSemester != null) {
                existingSemester.setTitle(newSemester.getTitle());
                existingSemester.setDescription(newSemester.getDescription());
            } else {
                // 새 GradeEntity 추가
                newSemester.setGradeEntity(existingGrade);
                semesterEntities.add(newSemester);
            }
        }

        // 삭제된 GradeEntity 제거
        semesterEntities.removeIf(semester -> !newSemesters.stream()
                .anyMatch(newSemester -> newSemester.getSemesterId().equals(semester.getSemesterId())));

        return existingGrade;
    }
}
