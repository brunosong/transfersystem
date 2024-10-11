package com.brunosong.transfer.system.aiservice.service.chap.port;

import com.brunosong.transfer.system.aiservice.domain.AiChap;

import java.util.List;
import java.util.Optional;

public interface AiChapRepository {

    List<AiChap> findAll();

    Optional<AiChap> findById(Long chapSeq);

    Optional<AiChap> save(AiChap aiChap);

}
