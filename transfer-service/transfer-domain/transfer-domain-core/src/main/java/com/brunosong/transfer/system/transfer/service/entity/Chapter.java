package com.brunosong.transfer.system.transfer.service.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Chapter {
    private Long chapSeq;
    private String chapTitle;
    private String chapType;
}
