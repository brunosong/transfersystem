package com.brunosong.transfersystem.aiservice.service.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfersystem.aiservice.service.chap.port.AiChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiChapService {

    private final AiChapRepository aiChapRepository;

    @Transactional("aiServiceJpaTransactionManager")
    public void save(AiChapDto.AiChapSaveDto saveDto) {

        Optional<AiChap> findChap = aiChapRepository.findById(saveDto.getAiChapSeq());

        AiChap aiChap = findChap.map(info -> {
            info.updateChapInfo(saveDto.toDomain());
            return info;
        }).orElseGet(() -> saveDto.toDomain());

        aiChapRepository.save(aiChap);

    }

}
