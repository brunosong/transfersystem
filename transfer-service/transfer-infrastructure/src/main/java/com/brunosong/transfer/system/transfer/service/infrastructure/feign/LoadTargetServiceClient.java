package com.brunosong.transfer.system.transfer.service.infrastructure.feign;

import com.brunosong.transfer.system.transfer.service.infrastructure.adapter.LearningMaterialApiModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "data-migration-service", url = "http://localhost:8001/api")
public interface LoadTargetServiceClient {

    @PostMapping("/v1/learning-metadata-migration")
    String sendMetadata(@RequestBody LearningMaterialApiModel learningMaterialApiModel);

}
