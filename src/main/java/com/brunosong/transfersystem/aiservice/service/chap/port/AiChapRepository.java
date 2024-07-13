package com.brunosong.transfersystem.aiservice.service.chap.port;

import com.brunosong.transfersystem.aiservice.domain.AiChap;

import java.util.Optional;

public interface AiChapRepository {

    Optional<AiChap> findById(Long chapSeq);

    Optional<AiChap> save(AiChap aiChap);

}
