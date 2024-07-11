package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import com.brunosong.transfersystem.aiservice.service.chap.port.ChapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChapPersistenceAdapter implements ChapRepository {

    private final ChapJpaRepository chapJpaRepository;

    @Override
    public Optional<Chap> findById(Long chapSeq) {
        return chapJpaRepository.findById(chapSeq).map(ChapEntity::toModel);
    }

    @Override
    public Optional<Chap> save(Chap chap) {
        return Optional.of(chapJpaRepository.save(ChapEntity.fromModel(chap)).toModel());
    }

}
