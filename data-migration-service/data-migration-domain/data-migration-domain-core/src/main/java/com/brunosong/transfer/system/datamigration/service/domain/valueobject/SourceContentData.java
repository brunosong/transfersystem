package com.brunosong.transfer.system.datamigration.service.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SourceContentData {
   private String sourceId;
   private ByteBuffer jsonData;
}
