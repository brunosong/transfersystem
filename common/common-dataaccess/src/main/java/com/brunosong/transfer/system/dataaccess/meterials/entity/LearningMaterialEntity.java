package com.brunosong.transfer.system.dataaccess.meterials.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "learning_materials")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningMaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @OneToMany(mappedBy = "learningMaterialEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LearningMaterialMetadataEntity> metadataList;

}