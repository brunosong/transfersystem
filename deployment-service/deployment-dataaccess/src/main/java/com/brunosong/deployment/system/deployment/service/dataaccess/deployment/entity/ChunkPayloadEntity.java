package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "deployment_chunk_payload")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChunkPayloadEntity {

    // 청크 상태 표와 같은 키(배포 ID + 청크 번호)라 ChunkEntityId 를 같이 쓴다
    @EmbeddedId
    private ChunkEntityId id;

    @Column(name = "content", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String content;

}
