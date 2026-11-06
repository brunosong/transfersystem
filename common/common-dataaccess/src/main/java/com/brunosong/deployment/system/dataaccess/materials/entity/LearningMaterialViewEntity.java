package com.brunosong.deployment.system.dataaccess.materials.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learning_materials")
public class LearningMaterialViewEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private int learningLevel;

    private List<Map<String,Object>> metadataList;

}