package com.brunosong.transfer.system.transfer.service.application.rest;

import com.brunosong.transfer.system.transfer.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/source-configs")
@RequiredArgsConstructor
public class SourceConfigController {

    private final TransferQueryService transferQueryService;

    @GetMapping
    public List<SourceConfigView> findSourceConfigs() {
        return transferQueryService.findSourceConfigs();
    }
}
