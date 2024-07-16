package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.domain.chap.MainChap;
import com.brunosong.transfersystem.main.dto.TranActionDto.TranActionReqDto;
import com.brunosong.transfersystem.main.repository.MainChapRepository;
import com.brunosong.transfersystem.main.repository.MainCourseRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
@ContextConfiguration
class TranServiceTest {

    @Mock
    MainChapRepository mainChapRepository;

    @Mock
    MainCourseRepository mainCourseRepository;

    @Mock
    MigrationServiceSelector migrationServiceSelector;

    @Mock
    MigrationService migrationService;

    @InjectMocks
    TranService tranService;

    @Test
    void AI_Service_이관로직이_정상적으로_실행된다() {

        List<MainChap> mainChaps = new ArrayList<>();
        MainChap model = MainChap.builder()
                .chapSeq(1L)
                .chapType(MainChap.MainChapTypeEnum.ENG)
                .chapTitle("테스트 차시")
                .build();
        mainChaps.add(model);

        TranActionReqDto tranActionDto = new TranActionReqDto();
        tranActionDto.setDbProfile("real");

        when(mainChapRepository.findAll()).thenReturn(mainChaps);
        when(migrationServiceSelector.getCurrentService()).thenReturn(migrationService);

        tranService.aiServiceTransferProcess(tranActionDto);

        verify(migrationServiceSelector, Mockito.times(1)).useAiService();
        verify(migrationServiceSelector, Mockito.times(2)).getCurrentService();
        verify(migrationServiceSelector, Mockito.times(1)).clearCurrentService();
        verify(migrationService, Mockito.times(1)).transferChap(any(List.class),any(String.class));
    }


    @Test
    void MainChap_을_찾지못하면_Exception_이_발생한다() {

        List<MainChap> mainChaps = new ArrayList<>();

        TranActionReqDto tranActionDto = new TranActionReqDto();
        tranActionDto.setDbProfile("real");

        when(mainChapRepository.findAll()).thenReturn(mainChaps);
        when(migrationServiceSelector.getCurrentService()).thenReturn(migrationService);

        Assertions.assertThatThrownBy(() -> {
            tranService.aiServiceTransferProcess(tranActionDto);
        }).isInstanceOf(RuntimeException.class);

    }

}