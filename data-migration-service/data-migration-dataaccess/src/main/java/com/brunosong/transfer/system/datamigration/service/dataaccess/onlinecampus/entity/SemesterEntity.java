package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "semester")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemesterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_id", nullable = false, unique = true)
    private String semesterId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private GradeEntity gradeEntity;

    @OneToMany(mappedBy = "semesterEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectEntity> subjectEntities = new ArrayList<>();

    public void setSubjectEntities(List<SubjectEntity> subjectEntities) {
        this.subjectEntities = subjectEntities;
    }

    public void addSubjectEntity(SubjectEntity subjectEntity) {
        subjectEntity.setSemesterEntity(this);
        subjectEntities.add(subjectEntity);
    }
}