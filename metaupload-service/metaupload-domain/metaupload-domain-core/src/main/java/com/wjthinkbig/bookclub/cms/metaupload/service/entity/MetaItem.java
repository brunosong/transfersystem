package com.wjthinkbig.bookclub.cms.metaupload.service.entity;

import lombok.*;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MetaItem implements Serializable {
    private String seq;
    private String grade;    // 학년
    private String term;    // 학기
    private String week;    // 주차
    private String prePro;    // 예습/진도
    private String classNum;    // cl< >ss
    private String courseCode;    // 과목
    private String subjectDay;    // 과목차시
    private String courseDetail;    // 대표단원
    private String area;            // 영역
    private String pubName;    // 출판사
    private String unitName;    // 단원명
    private int unitNum;    // 단원순서
    private int dayNum;    // 차시순서
    private String dayName;    // 차시명
    private String dayCont;    // 차시내용
    private String dayDisplay;    // 전체과목노출여부
    private String conerNum;    // 코너순서
    private String conerName;    // 코너명
    private String conerCont;    // 코너내용
    private String todayDisplay;    // 오늘의학습
    private String tocSeq;    // 목차고유번호
    private String tocNum;    // 목차순서
    private String tocName;    // 목차명
    private String tocType;    // 목차유형
    private String tocTypeCode;    // 목차유형코드
    private String qstViewYn;    // 문항뷰어여부
    private String allYn;    // 일괄채점여부
    private String mustYn;    // 필수학습여부
    private String qstCnt;    // 기본출제문항수
    private String maxCnt;    // 최대출제문항수
    private String preConer;    // 선행코너
    private String preMsg;    // 선행
    private String lvl1;    // 성취레벨1
    private String lvl2;    // 성취레벨2
    private String lvl3;    // 성취레벨3
    private String lvl4;    // 성취레벨4
    private String lvl5;    // 성취레벨5
    private Long levelTwoSeq; //2레벨 고유번호
    private String unitSeq;    // 단원고유번호
    private String daySeq;    // 차시고유번호
    private String conerSeq;    // 코너고유번호
    private String revisionYear; //개정연도
    private String revisionWhether; //개정여부
    private String viewerCssCode; //문항뷰어CSS코드
    private String goodsType; //자재 종류[학습]
    private String systemCode; //시스템코드_4레벨용

    //키즈통합
    private String fiveWeekFurther;   //5주차 진도
    private String preProgRelCourse; //이전 진도 연계 과목
    private String preProgRelLesn;   //이전 진도 연계 차시
    private String all100Code;         //올백 자제코드

    //DB에 컬럼이 존재하지 않음
    private String videoTitle;   // 영상제목
    private String videoFileName;   //영상파일명
    private String videoPath;        //영상경로

    //DB 엑셀에 존재하지 않음
    private String keyword;    // 영상제목
    private String creDtime;    // 영상파일명

    // 생성자
    public MetaItem(String seq, String grade, String term, String week, String prePro, String classNum, String courseCode,
                    String subjectDay, String courseDetail, String area, String pubName, String unitName, int unitNum,
                    int dayNum, String dayName, String dayCont, String dayDisplay, String conerNum, String conerName,
                    String conerCont, Long levelTwoSeq, String keyword) {
        this.seq = seq;
        this.grade = grade;
        this.term = term;
        this.week = week;
        this.prePro = prePro;
        this.classNum = classNum;
        this.courseCode = courseCode;
        this.subjectDay = subjectDay;
        this.courseDetail = courseDetail;
        this.area = area;
        this.pubName = pubName;
        this.unitName = unitName;
        this.unitNum = unitNum;
        this.dayNum = dayNum;
        this.dayName = dayName;
        this.dayCont = dayCont;
        this.dayDisplay = dayDisplay;
        this.conerNum = conerNum;
        this.conerName = conerName;
        this.conerCont = conerCont;
        this.levelTwoSeq = levelTwoSeq;
        this.keyword = keyword;
    }

}
