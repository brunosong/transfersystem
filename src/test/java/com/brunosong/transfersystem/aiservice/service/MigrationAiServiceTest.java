package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.ChapService;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class MigrationAiServiceTest {

    @Mock
    ChapService chapService;

    @Mock
    ChapMapper chapMapper;

    @InjectMocks
    MigrationAiService migrationAiService;


    @Test
    void test() {
        // given
        ChapTranDto chapTranDto = new ChapTranDto();
        chapTranDto.setChapSeq(1L);
        List<ChapTranDto> chapTranDtoList = new ArrayList<>();
        chapTranDtoList.add(chapTranDto);

        ChapSaveDto chapSaveDto = new ChapSaveDto();
        chapSaveDto.setChapSeq(1L);
        List<ChapSaveDto> chapSaveDtoList = new ArrayList<>();
        chapSaveDtoList.add(chapSaveDto);

        //stub
        when(chapMapper.toChapSaveDto(any())).thenReturn(chapSaveDto);

        migrationAiService.transferChap(chapTranDtoList,"real");

        verify(chapService, times(1)).save(any());


    }



}