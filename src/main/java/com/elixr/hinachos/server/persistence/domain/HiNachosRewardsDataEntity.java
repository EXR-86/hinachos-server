package com.elixr.hinachos.server.persistence.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "exr_hinachos_rewards")
public class HiNachosRewardsDataEntity {
    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "recognition_message")
    private String recognitionMessage;

    @Column(name = "time_of_recognition")
    private Date timeOfRecognition;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "hashtag_id")
    private String hashTagId;

    @Column(name = "redeemed")
    private String redeemed;

    @Column(name = "rewards_count")
    private long rewardsCount;

    /*
    @Column(name = "id", unique = true)
    private Long id;
    public void setId(Long id) {
        this.id = id;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    */
    @Id
    @Column(name = "id", unique = true)
    private String id; // Change type to UUID
}
