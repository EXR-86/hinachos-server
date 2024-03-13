package com.elixr.hinachos.server.persistence.repository;

import com.elixr.hinachos.server.persistence.domain.HiNachosRewardsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HiNachosRewardsRepository extends JpaRepository<HiNachosRewardsDataEntity, String> {
}
