package com.elixr.hinachos.server.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class HiNachosRewardAssignmentRequest {
    private String botDisplayName;
    private ChannelDetails channelDetails;
    private List<UserDataItem> receiverDataItemList;
    private UserDataItem senderDataItem;
    private RewardDetails rewardDetails;

    @Getter
    @Setter
    public static class UserDataItem {
        private String id;
        private String displayName;
        private String firstName;
        private String lastName;
        private String emailId;
    }

    @Getter
    @Setter
    public static class RewardDetails {
        private String recognitionMessage;
        private List<String> emojisList;
    }
    @Getter
    @Setter
    public static class ChannelDetails {
        private String channelId;
        private String channelName;
    }

    /**
     * Filter through request data to ensure there are no duplicate receiver details.
     */
    public void filterUniqueReceiverIds() {
        if (receiverDataItemList != null) {
            receiverDataItemList = receiverDataItemList.stream()
                    .collect(Collectors.toMap(UserDataItem::getId, Function.identity(),
                            (existing, replacement) -> existing))
                    .values().stream()
                    .collect(Collectors.toList());
        }
    }
}
