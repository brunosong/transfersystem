package com.brunosong.transfersystem.main.service;


import com.brunosong.transfersystem.main.domain.chap.MainChap;
import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.repository.MainChapRepository;
import com.brunosong.transfersystem.main.repository.MainCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranService {
    private final MainChapRepository mainChapRepository;

    private final ServiceSelector2 serviceSelector;

    public void aiServiceDbTransProcess(Long chapSep) throws InterruptedException {

        log.info("TransService is {} " , serviceSelector.getCurrentService().getClass().getName() );

        ChapTranDto chapTranDto = new ChapTranDto();

        TranDto tranDto = new TranDto();


        /* Course */

        //tranActionService.insertCourse(tranDto);


        /* Chapter */
        List<MainChap> mainChaps = mainChapRepository.findAll();

        List<ChapTranDto> chapTranDtoList = mainChaps.stream().map(ChapTranDto::fromEntity)
                .collect(Collectors.toList());

        log.info("aiServiceDbTransProcess is {} " , serviceSelector.getCurrentService().getClass().getName() );
        Thread.sleep(10000);
        serviceSelector.getCurrentService().insertChap(chapTranDtoList);
        log.info("aiServiceDbTransProcess is {} " , serviceSelector.getCurrentService().getClass().getName() );
        serviceSelector.getCurrentService().insertChap(chapTranDtoList);
        Thread.sleep(10000);
    }

    public void aiServiceKafkaTransProcess(Long chapSep) throws InterruptedException {

        log.info("TransService is {} " , serviceSelector.getCurrentService().getClass().getName() );

        List<MainChap> mainChaps = mainChapRepository.findAll();

        List<ChapTranDto> chapTranDtoList = mainChaps.stream().map(ChapTranDto::fromEntity)
                .collect(Collectors.toList());

        Optional<MainChap> byId = mainChapRepository.findById(1L);
        //tranActionService.insertChap(tranDto);
        Thread.sleep(10000);
        log.info("aiServiceKafkaTransProcess is {} " , serviceSelector.getCurrentService().getClass().getName() );
        serviceSelector.getCurrentService().insertChap(chapTranDtoList);
        log.info("aiServiceKafkaTransProcess is {} " , serviceSelector.getCurrentService().getClass().getName() );
        serviceSelector.getCurrentService().insertChap(chapTranDtoList);
        Thread.sleep(10000);



    }

}
