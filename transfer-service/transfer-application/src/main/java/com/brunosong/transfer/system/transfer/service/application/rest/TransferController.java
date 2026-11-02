package com.brunosong.transfer.system.transfer.service.application.rest;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferDetailView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSummaryView;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferApplicationService transferApplicationService;
    private final TransferQueryService transferQueryService;

    /**
     * 전송을 접수한다. 데이터를 청크로 나눠 아웃박스에 저장하고 바로 돌아오며,
     * 실제 반영은 비동기로 진행된다. 그래서 200 이 아니라 202 와 조회 위치를 돌려준다.
     */
    @PostMapping
    public ResponseEntity<CreateTransferResponse> createTransfer(@Valid @RequestBody TransferRequest transferRequest) {
        CreateTransferResponse response = transferApplicationService.executeTransfer(transferRequest);
        return ResponseEntity.accepted()
                .location(URI.create("/api/v1/transfers/" + response.transferId()))
                .body(response);
    }

    @GetMapping
    public PageView<TransferSummaryView> findTransfers(@RequestParam(required = false) TransferStatus status,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return transferQueryService.findTransfers(new TransferSearchCondition(status, page, size));
    }

    /** 상태별 전송 수. 목록 화면 상단의 집계 카드에 쓴다. */
    @GetMapping("/stats")
    public Map<String, Long> countByStatus() {
        return transferQueryService.countByStatus();
    }

    @GetMapping("/{transferId}")
    public TransferDetailView findTransfer(@PathVariable UUID transferId) {
        return transferQueryService.findTransfer(transferId);
    }
}
