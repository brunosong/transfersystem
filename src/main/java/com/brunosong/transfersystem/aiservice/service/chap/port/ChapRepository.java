package com.brunosong.transfersystem.aiservice.service.chap.port;

import com.brunosong.transfersystem.aiservice.domain.Chap;

import java.util.Optional;

public interface ChapRepository {

    Optional<Chap> findById(Long chapSeq);

    Optional<Chap> save(Chap chap);

}
