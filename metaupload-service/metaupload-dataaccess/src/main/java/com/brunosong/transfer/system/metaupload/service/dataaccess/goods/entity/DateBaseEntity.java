package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class DateBaseEntity {

    @CreationTimestamp
    private LocalDateTime creDtime;
    private String creId;
    private String creIp;

    @UpdateTimestamp
    private LocalDateTime updDtime;
    private String updIp;
    private String updId;

}
