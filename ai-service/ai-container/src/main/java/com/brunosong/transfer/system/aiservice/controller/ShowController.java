package com.brunosong.transfer.system.aiservice.controller;

import com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceDevDataSource;
import com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceRealDataSource;
import com.brunosong.transfer.system.aiservice.dto.chap.AiChapDto.AiChapRespDto;
import com.brunosong.transfer.system.aiservice.service.chap.AiChapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/* AiService에 데이터를 보여주기 위한 컨트롤러 ( API 규칙은 잠시 무시하겠습니다. )*/
@RestController
@RequiredArgsConstructor
public class ShowController {

    private final AiChapService aiChapService;

    @UseAiServiceRealDataSource
    @GetMapping("/api/ai-chap/real")
    public ResponseEntity<?> getAllRealAiChaps() {
        List<AiChapRespDto> aiChaps = aiChapService.findAll();
        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
    }


    @UseAiServiceDevDataSource
    @GetMapping("/api/ai-chap/dev")
    public ResponseEntity<?> getAllDevAiChaps() {
        List<AiChapRespDto> aiChaps = aiChapService.findAll();
        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
    }


    @UseAiServiceRealDataSource
    @GetMapping("/api/ai-course/real")
    public ResponseEntity<?> getAllRealAiCourses() {
        List<AiChapRespDto> aiChaps = aiChapService.findAll();
        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
    }


    @UseAiServiceDevDataSource
    @GetMapping("/api/ai-course/dev")
    public ResponseEntity<?> getAllDevAiCourses() {
        List<AiChapRespDto> aiChaps = aiChapService.findAll();
        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
    }

}
