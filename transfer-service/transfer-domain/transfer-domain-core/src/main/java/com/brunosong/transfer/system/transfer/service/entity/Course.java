package com.brunosong.transfer.system.transfer.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long courseSeq;
    private String courseName;
}
