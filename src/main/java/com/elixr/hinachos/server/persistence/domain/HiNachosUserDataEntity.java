package com.elixr.hinachos.server.persistence.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "exr_hinachos_user_details")
public class HiNachosUserDataEntity extends HiNachosMasterDataModel{

    /*
    @Id
    @Column(name = "id", unique = true)
    private String id; // Change type to UUID
    @Column(name = "external_id", unique = true) //Shiju
    private String userId;

    @Column(name = "name") //shiju
    private String displayName;
    @Column(name = "first_name")
    */
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email_id")
    private String emailId;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "modified_date")
    private Date modifiedDate;
    @Column(name = "comments")
    private String comments;
}
