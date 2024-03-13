package com.elixr.hinachos.server.persistence.repository;

import com.elixr.hinachos.server.persistence.domain.HiNachosMasterDataModel;

public interface HiNachosMasterRepositoryInterface<T extends HiNachosMasterDataModel> {
    T findByExternalId(String channelId);
}
