package com.brunosong.transfer.system.ai.service.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Chapter {
    private Long chapSeq;
    private Long courseSeq;
    private String chapTitle;
    private String chapType;
    private int chapOrder;
}
