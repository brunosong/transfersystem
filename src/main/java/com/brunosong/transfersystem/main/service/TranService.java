package com.brunosong.transfersystem.main.service;


import com.brunosong.transfersystem.main.domain.chap.MainChap;
import com.brunosong.transfersystem.main.dto.TranActionDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranEnum;
import com.brunosong.transfersystem.main.repository.MainChapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranService {
    private final MainChapRepository mainChapRepository;

    private final MigrationServiceSelector migrationServiceSelector;

    public void aiServiceTransferProcess(TranActionDto tranActionDto) {
        migrationServiceSelector.useAiService();
        log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );

        /* Chapter */
        List<MainChap> mainChaps = mainChapRepository.findAll();

        List<ChapTranDto> chapTranDtoList = mainChaps.stream().map(ChapTranDto::fromEntity)
                .collect(Collectors.toList());

        migrationServiceSelector.getCurrentService().transferChap(chapTranDtoList, tranActionDto.getDbProfile());

        migrationServiceSelector.clearCurrentService();
    }


    public void aiKafkaServiceTransferProcess(TranActionDto tranActionDto) {
        migrationServiceSelector.useAiKafkaService();
        log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );





        migrationServiceSelector.clearCurrentService();
    }


}
