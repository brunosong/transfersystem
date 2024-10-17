package com.brunosong.transfer.system.dataaccess.meterials.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learningMaterial")
public class LearningMaterialMongoEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private int learningLevel;

    private List<Map<String,Object>> metadataList;

}