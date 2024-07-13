package com.brunosong.transfersystem.aiservice.service.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.aiservice.service.chap.port.AiChapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChapAiChapServiceTestEntity {

    @Mock
    AiChapRepository aiChapRepository;

    @InjectMocks
    AiChapService aiChapService;

    @Test
    void findById로_값이_찾아져도_SAVE_메소드가_정상실행된다(){

        //given
        AiChap aiChap = AiChap.builder().aiChapSeq(1L).build();

        AiChapSaveDto aiChapSaveDto = new AiChapDto.AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);
        aiChapSaveDto.setAiChapType("ENG");

        //stub
        when(aiChapRepository.findById(1L)).thenReturn(Optional.of(aiChap));
        when(aiChapRepository.save(any(AiChap.class))).thenReturn(Optional.empty());

        //when
        aiChapService.save(aiChapSaveDto);

        //then
        verify(aiChapRepository, times(1) ).findById(1L);
        verify(aiChapRepository, times(1) ).save(aiChap);

    }



    @Test
    void findById가_EMPTY_여도_SAVE_메소드가_정상실행된다(){

        //given
        AiChapDto.AiChapSaveDto aiChapSaveDto = new AiChapDto.AiChapSaveDto();
        aiChapSaveDto.setAiChapSeq(1L);
        aiChapSaveDto.setAiChapType("ENG");

        //stub
        when(aiChapRepository.findById(1L)).thenReturn(Optional.empty());
        when(aiChapRepository.save(any(AiChap.class))).thenReturn(Optional.empty());

        //when
        aiChapService.save(aiChapSaveDto);

        //then
        verify(aiChapRepository, times(1) ).findById(1L);
        verify(aiChapRepository, times(1) ).save(any(AiChap.class));

    }

}