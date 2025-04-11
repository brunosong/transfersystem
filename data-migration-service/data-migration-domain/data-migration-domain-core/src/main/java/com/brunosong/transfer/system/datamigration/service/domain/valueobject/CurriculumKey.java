package com.brunosong.transfer.system.datamigration.service.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurriculumKey {

    CURRICULUM("curriculum",1),
    GRADE("grade",2),
    SEMESTER("semester",3),
    SUBJECT("subject",4),
    UNIT("unit",5),
    LESSON("lesson",6);

    private final String key;
    private final int level;
}
