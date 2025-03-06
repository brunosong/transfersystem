package com.brunosong.transfer.system.transfer.service.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningMaterialMetadata {
    private String attributeName;
    private String attributeValue;
}
