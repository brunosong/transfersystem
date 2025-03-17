package com.brunosong.transfer.system.datamigration.service.dataaccess.datamigrationinfo.entity;

import com.brunosong.transfer.system.dataaccess.common.entity.BaseTimeEntity;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationDbEnvironment;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DestinationServiceStatus;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.MigrationMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "data_migration_info")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataMigrationInfoEntity extends BaseTimeEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DestinationServiceStatus destinationService;

    @Enumerated(EnumType.STRING)
    private DestinationDbEnvironment dbEnvironment;

    @Enumerated(EnumType.STRING)
    private MigrationMode migrationMode;

    private String dbUrl;
    private String userName;
    private String password;

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
