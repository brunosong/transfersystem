package com.brunosong.transfer.system.dataaccess.meterials.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "learning_materials")
public class LearningMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @OneToMany(mappedBy = "learningMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LearningMaterialMetadata> metadataList;

}