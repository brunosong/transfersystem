package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.AiChapService;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class MigrationAiServiceTestChap {

    @Mock
    AiChapService aiChapService;

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

        AiChapDto.AiChapSaveDto aiChapSaveDto = new AiChapDto.AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);
        List<AiChapDto.AiChapSaveDto> aiChapSaveDtoList = new ArrayList<>();
        aiChapSaveDtoList.add(aiChapSaveDto);

        //stub
        when(chapMapper.toChapSaveDto(any())).thenReturn(aiChapSaveDto);

        migrationAiService.transferChap(chapTranDtoList,"real");

        verify(aiChapService, times(1)).save(any());


    }



}