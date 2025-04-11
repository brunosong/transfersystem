package com.brunosong.transfer.system.transfer.service.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurriculumBaseKey {

    CURRICULUM("curriculum",0),
    LEVEL1("grade",1),
    LEVEL2("semester",2),
    LEVEL3("subject",3),
    LEVEL4("unit",4),
    LEVEL5("lesson",5);

    private final String key;
    private final int level;
}
