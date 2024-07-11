package com.brunosong.transfersystem.aiservice.service.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.aiservice.service.chap.port.ChapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapServiceTest {

    @Mock
    ChapRepository chapRepository;

    @InjectMocks
    ChapService chapService;

    @Test
    void findById로_값이_찾아져도_SAVE_메소드가_정상실행된다(){

        //given
        Chap chap = Chap.builder().chapSeq(1L).build();

        ChapSaveDto chapSaveDto = new ChapSaveDto();
        chapSaveDto.setChapSeq(1L);

        //stub
        when(chapRepository.findById(1L)).thenReturn(Optional.of(chap));
        when(chapRepository.save(any(Chap.class))).thenReturn(Optional.empty());

        //when
        chapService.save(chapSaveDto);

        //then
        verify(chapRepository, times(1) ).findById(1L);
        verify(chapRepository, times(1) ).save(chap);

    }



    @Test
    void findById가_EMPTY_여도_SAVE_메소드가_정상실행된다(){

        //given
        ChapSaveDto chapSaveDto = new ChapSaveDto();
        chapSaveDto.setChapSeq(1L);

        //stub
        when(chapRepository.findById(1L)).thenReturn(Optional.empty());
        when(chapRepository.save(any(Chap.class))).thenReturn(Optional.empty());

        //when
        chapService.save(chapSaveDto);

        //then
        verify(chapRepository, times(1) ).findById(1L);
        verify(chapRepository, times(1) ).save(any(Chap.class));

    }

}