package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity;

import com.brunosong.transfer.system.dataaccess.common.entity.BaseTimeEntity;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystem;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "datamigrations")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataMigrationInfoEntity extends BaseTimeEntity {

    @Id
    @Column(name = "datamigration_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TargetSystem targetSystem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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
