package com.elixr.hinachos.server.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class HiNachosRewardsSummarySearchCriteria {
    private String userType;
    private String channelId;
    private Timestamp startDate;
    private Timestamp endDate;
    private Long pageNumber;
}
