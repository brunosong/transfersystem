package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grade")
@Getter
@Setter
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_id", nullable = false, unique = true)
    private String gradeId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private CurriculumEntity curriculumEntity;

    @OneToMany(mappedBy = "gradeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SemesterEntity> semesterEntities = new ArrayList<>();
}
