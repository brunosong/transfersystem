package com.brunosong.transfersystem.aiservice.service.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.aiservice.service.chap.port.ChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChapService {

    private final ChapRepository chapRepository;

    @Transactional("aiServiceJpaTransactionManager")
    public void save(ChapSaveDto saveDto) {

        Optional<Chap> findChap = chapRepository.findById(saveDto.getChapSeq());

        Chap chap = findChap.map(info -> {
            info.updateChapInfo(saveDto.toDomain());
            return info;
        }).orElseGet(() -> saveDto.toDomain());

        chapRepository.save(chap);

    }

}
