package com.brunosong.transfer.system.main;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Map;

@EnableMongoRepositories(basePackages = "com.brunosong.transfer.system.dataaccess")
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class TransferSystemApplication {

    @Bean
    CommandLineRunner initData(LearningMaterialMongoRepository learningMaterialMongoRepository) {
        return args -> {
            if (learningMaterialMongoRepository.count() == 0) { // 컬렉션이 비어 있을 때만 초기화
                LearningMaterialViewEntity material1 = LearningMaterialViewEntity.builder()
                        .title("Introduction to Java")
                        .description("Basic Java programming concepts")
                        .learningLevel(1)
                        .metadataList(List.of(
                                Map.of("author", "John Doe", "pages", 120),
                                Map.of("format", "PDF", "language", "English")
                        ))
                        .build();

                LearningMaterialViewEntity material2 = LearningMaterialViewEntity.builder()
                        .title("Advanced Java")
                        .description("In-depth Java programming techniques")
                        .learningLevel(3)
                        .metadataList(List.of(
                                Map.of("author", "Jane Smith", "pages", 300),
                                Map.of("format", "eBook", "language", "English")
                        ))
                        .build();

                LearningMaterialViewEntity material3 = LearningMaterialViewEntity.builder()
                        .title("Spring Boot Basics")
                        .description("Getting started with Spring Boot")
                        .learningLevel(2)
                        .metadataList(List.of(
                                Map.of("author", "Alex Brown", "pages", 200),
                                Map.of("format", "Video", "language", "English")
                        ))
                        .build();

                learningMaterialMongoRepository.saveAll(List.of(material1, material2, material3));
                System.out.println("초기 LearningMaterialView 데이터가 MongoDB에 추가되었습니다.");
            } else {
                System.out.println("LearningMaterialView 데이터가 이미 존재합니다.");
            }
        };
    }
    public static void main(String[] args) {
        SpringApplication.run(TransferSystemApplication.class, args);
    }


    /* 실제 운영에 쓰이지 않지만 이 프로젝트에선 프로젝트가 기동할때 카푸카를 임베디드로 기동한다. */
//    @Bean
//    @ConditionalOnProperty(value = "spring.kafka.main-start" , havingValue = "true")
//    public EmbeddedKafkaBroker embeddedKafka() {
//        return new EmbeddedKafkaBroker(1, true, 1, "brunosong_topic")
//                .kafkaPorts(9092);
//    }
}
