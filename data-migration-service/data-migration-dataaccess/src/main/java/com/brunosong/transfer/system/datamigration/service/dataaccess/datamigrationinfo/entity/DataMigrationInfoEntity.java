package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity;

import com.brunosong.transfer.system.dataaccess.common.entity.BaseTimeEntity;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Entity
@Table(name = "datamigrations", schema = "datamigration")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataMigrationInfoEntity extends BaseTimeEntity {

    @Id
    @Column(name = "datamigration_id")
    private Long id;

    @Column(name = "target_system")
    private String targetSystem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "target_system_environment")
    private TargetSystemEnvironment targetSystemEnvironment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataMigrationInfoEntity that = (DataMigrationInfoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
