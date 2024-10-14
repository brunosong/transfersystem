package com.brunosong.transfer.system.ai.service.application.rest;

import com.brunosong.transfer.system.ai.service.ports.input.AiTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiTransferService aiTransferService;

//    private final AiChapService aiChapService;
//
//    @UseAiServiceRealDataSource
//    @GetMapping("/api/ai-chap/real")
//    public ResponseEntity<?> getAllRealAiChaps() {
//        List<AiChapRespDto> aiChaps = aiChapService.findAll();
//        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
//    }
//
//
//    @UseAiServiceDevDataSource
//    @GetMapping("/api/ai-chap/dev")
//    public ResponseEntity<?> getAllDevAiChaps() {
//        List<AiChapRespDto> aiChaps = aiChapService.findAll();
//        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
//    }
//
//
//    @UseAiServiceRealDataSource
//    @GetMapping("/api/ai-course/real")
//    public ResponseEntity<?> getAllRealAiCourses() {
//        List<AiChapRespDto> aiChaps = aiChapService.findAll();
//        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
//    }
//
//
//    @UseAiServiceDevDataSource
//    @GetMapping("/api/ai-course/dev")
//    public ResponseEntity<?> getAllDevAiCourses() {
//        List<AiChapRespDto> aiChaps = aiChapService.findAll();
//        return new ResponseEntity<>(aiChaps, HttpStatus.OK);
//    }

}
