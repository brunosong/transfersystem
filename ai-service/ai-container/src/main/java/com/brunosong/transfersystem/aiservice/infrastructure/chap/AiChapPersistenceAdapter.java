package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import com.brunosong.transfersystem.aiservice.service.chap.port.AiChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AiChapPersistenceAdapter implements AiChapRepository {

    private final AiChapJpaRepository aiChapJpaRepository;
    
    /* 원래는 이렇게 전체를 다 가져오지 않습니다. 빠르게 예제를 작성하기 위함입니다. */
    // Todo. findAll 메소드 바꿔야함
    @Override
    public List<AiChap> findAll() {
        return aiChapJpaRepository.findAll().stream()
                .map(AiChapEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<AiChap> findById(Long chapSeq) {
        return aiChapJpaRepository.findById(chapSeq).map(AiChapEntity::toModel);
    }

    @Override
    public Optional<AiChap> save(AiChap aiChap) {
        return Optional.of(aiChapJpaRepository.save(AiChapEntity.fromModel(aiChap)).toModel());
    }

}
