package com.elixr.hinachos.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HiNachosRewardsDataItem {
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    private String timeOfRecognition;
    private long totalRewardsCount;
}
