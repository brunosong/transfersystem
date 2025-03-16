package com.brunosong.transfer.system.transfer.service.dataaccess.source.entity;

import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "sourceconfig")
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class SourceConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sourceconfig_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "db_type",  columnDefinition = "enum")
    private DbType dbType;

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String tableOrCollection;
}
