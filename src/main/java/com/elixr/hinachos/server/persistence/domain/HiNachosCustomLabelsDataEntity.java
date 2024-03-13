package com.elixr.hinachos.server.persistence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "exr_hinachos_custom_labels")
public class HiNachosCustomLabelsDataEntity {
    @Id
    @Column(name = "id", unique = true)
    private String id;
    @Column(name = "domain_name")
    private String domainName;
    @Column(name = "custom_labels_json")
    private String customLabelsJsonString;
}
