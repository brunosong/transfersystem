package com.brunosong.transfer.system.transfer.service.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Course {
    private Long courseSeq;
    private String courseName;
}
