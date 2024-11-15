package com.brunosong.transfer.system.metaupload.service.domain;

import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.exception.MetaItemConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
public class MetaUploadConvertHelper {

    public List<MetaItem> excelToMetaItemConvert(List<LinkedHashMap<String, Object>> excelMapList) {

        List<MetaItem> metaItemList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < excelMapList.size(); i++) {
            LinkedHashMap<String, Object> row = excelMapList.get(i);
            try {
                MetaItem bulkVo = new MetaItem();
                bulkVo.setGrade((String) row.get("학년"));
                bulkVo.setTerm((String) row.get("학기"));
                bulkVo.setWeek((String) row.get("주차"));
                bulkVo.setPrePro((String) row.get("예습/진도"));
                bulkVo.setClassNum((String) row.get("cl< >ss"));
                bulkVo.setCourseCode((String) row.get("과목"));
                bulkVo.setSubjectDay(((String) row.get("과목차시")).replace("'", ""));
                bulkVo.setCourseDetail((String) row.get("대표단원(한글/국어)"));
                bulkVo.setArea((String) row.get("영역"));

                /* 개정으로 인해 추가 2023 10 START */
                bulkVo.setRevisionYear((String) row.get("개정연도"));
                bulkVo.setRevisionWhether((String) row.get("개정여부"));
                /* 개정으로 인해 추가 2023 10 END */

                bulkVo.setPubName((String) row.get("출판사"));
                bulkVo.setUnitName((String) row.get("단원명"));
                bulkVo.setUnitNum(Integer.parseInt((String) row.get("단원순서")));
                bulkVo.setDayNum(Integer.parseInt((String) row.get("차시순서")));
                bulkVo.setDayName((String) row.get("차시명"));
                bulkVo.setDayCont((String) row.get("차시내용(수정)"));
                bulkVo.setDayDisplay((String) row.get("전체과목노출여부(차시)"));
                bulkVo.setConerNum((String) row.get("코너순서"));
                bulkVo.setConerName((String) row.get("코너명"));
                bulkVo.setConerCont((String) row.get("코너내용"));
                bulkVo.setTodayDisplay((String) row.get("오늘의학습노출여부"));
                bulkVo.setTocSeq((String) row.get("목차고유번호"));
                bulkVo.setTocNum((String) row.get("목차순서"));
                bulkVo.setTocName((String) row.get("목차명"));
                bulkVo.setTocType((String) row.get("목차유형"));
                bulkVo.setTocTypeCode((String) row.get("목차유형코드"));
                bulkVo.setQstViewYn((String) row.get("문항뷰어여부"));
                bulkVo.setAllYn((String) row.get("일괄채점여부"));
                bulkVo.setMustYn((String) row.get("필수학습여부(완료체크)"));
                bulkVo.setQstCnt((String) row.get("기본출제문항수"));
                bulkVo.setMaxCnt((String) row.get("최대출제문항수"));
                bulkVo.setPreConer((String) row.get("선행코너"));
                bulkVo.setPreMsg((String) row.get("선행메시지"));
                bulkVo.setLvl1((String) row.get("레벨1(100%)"));
                bulkVo.setLvl2((String) row.get("레벨2(99~50%)"));
                bulkVo.setLvl3((String) row.get("레벨3(49%~0%)"));
                bulkVo.setLvl4((String) row.get("레벨4"));
                bulkVo.setLvl5((String) row.get("레벨5"));

                // 필수 값으로 변경
                bulkVo.setLevelTwoSeq(Long.parseLong((String) row.get("2레벨고유번호")));

                bulkVo.setUnitSeq((String) row.get("단원고유번호"));
                bulkVo.setDaySeq((String) row.get("차시고유번호"));
                bulkVo.setConerSeq((String) row.get("코너고유번호"));
                bulkVo.setViewerCssCode((String) row.get("문항뷰어CSS코드"));
                bulkVo.setGoodsType((String) row.get("자재 종류[학습]_6레벨용"));
                bulkVo.setSystemCode((String) row.get("시스템코드_4레벨용"));

                // 키즈통합
                bulkVo.setFiveWeekFurther((String) row.get("5주차 진도"));
                bulkVo.setPreProgRelCourse((String) row.get("이전 진도 연계 과목"));
                bulkVo.setPreProgRelLesn((String) row.get("이전 진도 연계 차시"));
                bulkVo.setAll100Code((String) row.get("올백 교재 자재코드"));

                metaItemList.add(bulkVo);
            } catch (Exception e) {
                errorMessages.add("Row " + (i + 1) + ": Error converting data - " + e.getMessage());
            }

            if (!errorMessages.isEmpty()) {
                // 필요하다면, 변환은 완료되었지만 일부 에러가 있었다고 응답에 포함할 수 있음
                throw new MetaItemConvertException("Some items could not be converted", errorMessages);
            }
        }

        return metaItemList;
    }

}
