package com.brunosong.transfer.system.dataaccess.meterials.entity;


import javax.persistence.*;

@Entity
@Table(name = "learning_material_metadata")
public class LearningMaterialMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private LearningMaterial learningMaterial;

    private String attributeName;
    private String attributeValue;

}