package com.brunosong.transfer.system.datamigration.service.application.rest;

import com.brunosong.transfer.system.datamigration.service.dto.create.CreateLearningMetaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor

public class CreateLearningMetaController {

    //private final CreateLearningMetaService createLearningMetaService;

    @PostMapping("/learning-metadata")
    public ResponseEntity<CreateLearningMetaCommand> createLearningMeta(@RequestBody CreateLearningMetaCommand createLearningMetaCommand) {
//        log.info("Creating customer with username: {}", createCustomerCommand.getUsername());
//        CreateCustomerResponse response = customerApplicationService.createCustomer(createCustomerCommand);
        return ResponseEntity.ok(null);
    }

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
