package com.elixr.hinachos.server.persistence.repository;

import com.elixr.hinachos.server.persistence.domain.HiNachosChannelDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiNachosChannelRepository extends HiNachosMasterRepositoryInterface<HiNachosChannelDataEntity>, JpaRepository<HiNachosChannelDataEntity, String> {
}
