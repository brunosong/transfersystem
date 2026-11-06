package com.brunosong.deployment.system.deployment.service.dataaccess.source.repository;

import com.brunosong.deployment.system.deployment.service.dataaccess.source.entity.SourceConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceConfigJpaRepository extends JpaRepository<SourceConfigEntity, Long> {

    /**
     * 화면에 보여 줄 원본 설정 목록. 접속 계정은 고르지 않는다.
     * 열거형 컬럼을 문자열로 읽어, 코드에 없는 값이 섞여 있어도 목록 전체가 깨지지 않게 한다.
     */
    @Query(value = "select sourceconfig_id as sourceConfigId, cast(source_type as varchar) as sourceType, "
            + "cast(db_type as varchar) as dbType, host, port, database as databaseName, "
            + "table_or_collection as tableOrCollection from sourceconfig order by sourceconfig_id",
            nativeQuery = true)
    List<SourceConfigSummary> findAllSummaries();

    interface SourceConfigSummary {
        Long getSourceConfigId();

        String getSourceType();

        String getDbType();

        String getHost();

        Integer getPort();

        String getDatabaseName();

        String getTableOrCollection();
    }
}
