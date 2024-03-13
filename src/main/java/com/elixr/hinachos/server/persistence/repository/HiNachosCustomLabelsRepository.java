package com.elixr.hinachos.server.persistence.repository;

import com.elixr.hinachos.server.persistence.domain.HiNachosCustomLabelsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiNachosCustomLabelsRepository extends JpaRepository<HiNachosCustomLabelsDataEntity, String> {
    HiNachosCustomLabelsDataEntity findByDomainName(String domainName);
}
