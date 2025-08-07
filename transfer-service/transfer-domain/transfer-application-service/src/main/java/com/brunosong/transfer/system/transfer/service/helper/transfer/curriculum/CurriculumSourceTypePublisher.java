package com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum;

import com.brunosong.transfer.system.transfer.service.config.annotation.SourceTypeSelector;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.transfer.SourceTypePublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.CurriculumBaseKey;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@SourceTypeSelector(
        sourceType = { SourceType.CURRICULUM }
)
public class CurriculumSourceTypePublisher implements SourceTypePublisher {

    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @Override
    public void typePublisher(Transfer transfer) {

    }

}
