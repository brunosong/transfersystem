package com.brunosong.transfer.system.datamigration.service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    private Long chapSeq;
    private Long courseSeq;
    private String chapTitle;
    private String chapType;
    private int chapOrder;
}
