package om.brunosong.transfer.system.dataupload.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;

import java.util.List;
import java.util.Map;

public class LearningMaterial extends AggregateRoot<LearningMaterialId> {

    private String title;
    private String description;
    private int learningLevel;

    private List<Map<String,Object>> metadataList;

}
