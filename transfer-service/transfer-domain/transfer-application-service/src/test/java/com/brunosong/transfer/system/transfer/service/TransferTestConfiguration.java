package com.brunosong.transfer.system.transfer.service;
//
//import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.ChapterRequestPublisher;
//import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.CourseRequestPublisher;
//import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
//import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
//import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSendService;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class TransferTestConfiguration {

    @Bean
    public TransferLogRepository transferLogRepository() {
        return Mockito.mock(TransferLogRepository.class);
    }

    @Bean
    public LearningMaterialRepository learningMaterialRepository() {
        return Mockito.mock(LearningMaterialRepository.class);
    }

    @Bean
    public LearningMaterialApiSendService learningMaterialApiSendService() {
        return Mockito.mock(LearningMaterialApiSendService.class);
    }

//    @Bean
//    public CourseRepository courseRepository() {
//        return Mockito.mock(CourseRepository.class);
//    }
//
//    @Bean
//    public ChapterRepository chapterRepository() {
//        return Mockito.mock(ChapterRepository.class);
//    }
//
//    @Bean
//    public SaveTargetRepository saveTargetRepository() {
//        return Mockito.mock(SaveTargetRepository.class);
//    }
//
//    @Bean
//    public CourseRequestPublisher courseRequestPublisher() {
//        return Mockito.mock(CourseRequestPublisher.class);
//    }
//
//    @Bean
//    public ChapterRequestPublisher chapterRequestPublisher() {
//        return Mockito.mock(ChapterRequestPublisher.class);
//    }

}
