package com.elixr.hinachos.server.persistence.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class HiNachosMasterDataModel {
    @Id
    @Column(name = "id", unique = true)
    private String id;
    @Column(name = "external_id", unique = true)
    private String externalId;
    @Column(name = "name")
    private String name;
}
