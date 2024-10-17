package om.brunosong.transfer.system.dataupload.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.BaseEntity;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;

public class LearningMaterialMetadata extends BaseEntity<LearningMaterialId> {

    private String attributeName;
    private String attributeValue;

    private LearningMaterialMetadata(Builder builder) {
        setId(builder.id);
        this.attributeName = builder.attributeName;
        this.attributeValue = builder.attributeValue;
    }

    public static final class Builder {
        private LearningMaterialId id;
        private String attributeName;
        private String attributeValue;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(LearningMaterialId id) {
            this.id = id;
            return this;
        }

        public Builder attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder attributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
            return this;
        }

        public LearningMaterialMetadata build() {
            return new LearningMaterialMetadata(this);
        }
    }
}
