package com.brunosong.transfersystem.aiservice.service.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapRespDto;
import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.aiservice.service.chap.port.AiChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/* AiChapService를 헥사고날 아키텍쳐를 적용하고 도메인을 고립하기 위해서 또하나의 인터페이스를 정의해서 사용가능
*  하지만 어디까지 적용할것인가에 대한 선택이기에 이 얘제에서는 추후에 적용또는 패스하기로 한다.
*/
@Service
@RequiredArgsConstructor
public class AiChapService {

    private final AiChapRepository aiChapRepository;

    public List<AiChapRespDto> findAll() {
        List<AiChap> aiChapList = aiChapRepository.findAll();
        return aiChapList.stream()
                .map(AiChapRespDto::fromDomain).collect(Collectors.toList());
    }

    @Transactional("aiServiceJpaTransactionManager")
    public void save(AiChapSaveDto saveDto) {

        Optional<AiChap> findChap = aiChapRepository.findById(saveDto.getAiChapSeq());

        AiChap aiChap = findChap.map(info -> {
            info.updateChapInfo(saveDto.toDomain());
            return info;
        }).orElseGet(() -> saveDto.toDomain());

        aiChapRepository.save(aiChap);

    }

}
