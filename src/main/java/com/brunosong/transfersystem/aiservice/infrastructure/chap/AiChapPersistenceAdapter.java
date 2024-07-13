package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.service.chap.port.AiChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiChapPersistenceAdapter implements AiChapRepository {

    private final AiChapJpaRepository aiChapJpaRepository;

    @Override
    public Optional<AiChap> findById(Long chapSeq) {
        return aiChapJpaRepository.findById(chapSeq).map(AiChapEntity::toModel);
    }

    @Override
    public Optional<AiChap> save(AiChap aiChap) {
        return Optional.of(aiChapJpaRepository.save(AiChapEntity.fromModel(aiChap)).toModel());
    }

}
