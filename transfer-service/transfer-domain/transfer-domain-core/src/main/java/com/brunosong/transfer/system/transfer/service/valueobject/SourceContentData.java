package com.brunosong.transfer.system.transfer.service.valueobject;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class SourceContentData {
    private String sourceId;
    private byte[] jsonData;
}
