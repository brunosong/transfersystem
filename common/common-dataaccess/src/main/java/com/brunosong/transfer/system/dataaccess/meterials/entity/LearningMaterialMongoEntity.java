package com.brunosong.transfer.system.dataaccess.meterials.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "learningMaterial")
public class LearningMaterialMongoEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private int learningLevel;

    private List<Map<String,Object>> metadataList;

}