package com.brunosong.transfer.system.transfer.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "load-target-Service", url = "http://localhost:8001/api")
public interface LoadTargetServiceClient {

    @PostMapping("/v1/learning-metadata")
    String getDataById(@PathVariable("id") String id);

}
