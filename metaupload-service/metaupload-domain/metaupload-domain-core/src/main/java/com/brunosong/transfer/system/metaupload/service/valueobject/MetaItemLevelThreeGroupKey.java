package com.brunosong.transfer.system.metaupload.service.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class MetaItemLevelThreeGroupKey {
    private String courseCode;  // 과목
    private String grade;       // 학년
    private String term;        // 학기
    private String unitName;    // 단원명
    private int unitNum;     // 단원순서
    private String prePro;      // 예습/진도
    private String pubName;     // 출판사
    private String keyword;    // 영상제목

    public MetaItemLevelThreeGroupKey(String courseCode, String grade, String term, String unitName, int unitNum, String prePro, String pubName, String keyword) {
        this.courseCode = courseCode;
        this.grade = grade;
        this.term = term;
        this.unitName = unitName;
        this.unitNum = unitNum;
        this.prePro = prePro;
        this.pubName = pubName;
        this.keyword = keyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaItemLevelThreeGroupKey that = (MetaItemLevelThreeGroupKey) o;
        return  Objects.equals(unitNum , that.unitNum) &&
                Objects.equals(courseCode, that.courseCode) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(term, that.term) &&
                Objects.equals(unitName, that.unitName) &&
                Objects.equals(prePro, that.prePro) &&
                Objects.equals(pubName, that.pubName) &&
                Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, grade, term, unitName, unitNum, prePro, pubName, keyword);
    }
}
