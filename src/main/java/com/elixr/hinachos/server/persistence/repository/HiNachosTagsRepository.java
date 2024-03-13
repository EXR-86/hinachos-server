package com.elixr.hinachos.server.persistence.repository;

import com.elixr.hinachos.server.persistence.domain.HiNachosHashTagDataEntity;
import com.elixr.hinachos.server.persistence.domain.HiNachosUserDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiNachosTagsRepository extends HiNachosMasterRepositoryInterface<HiNachosHashTagDataEntity>, JpaRepository<HiNachosHashTagDataEntity, String> {
}
