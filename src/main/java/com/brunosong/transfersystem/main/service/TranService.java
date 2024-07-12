package com.brunosong.transfersystem.main.service;


import com.brunosong.transfersystem.main.domain.chap.MainChap;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.repository.MainChapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranService {
    private final MainChapRepository mainChapRepository;

    private final MigrationServiceSelector serviceSelector;


    public void aiServiceDbTransProcess(Long chapSep) {

        log.info("TransService is {} " , serviceSelector.getCurrentService().getClass().getName() );

        /* Chapter */
        List<MainChap> mainChaps = mainChapRepository.findAll();

        List<ChapTranDto> chapTranDtoList = mainChaps.stream().map(ChapTranDto::fromEntity)
                .collect(Collectors.toList());

        serviceSelector.getCurrentService().transferChap(chapTranDtoList);

    }

    public void aiServiceKafkaTransProcess(Long chapSep) throws InterruptedException {




    }

}
