package com.brunosong.transfer.system.datamigration.service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long courseSeq;
    private String courseName;
    private List<Chapter> chapterList;
}
