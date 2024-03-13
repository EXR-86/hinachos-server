package com.elixr.hinachos.server.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Setter
public class HiNachosRewardsSearchCriteria {
    private long pageIndex;
    //ID of user who is the receiver
    private String userId;
    //ID of user who is the sender
    private String senderId;
    private String redeemedOption;
    private Date startDate;
    private Date endDate;

}
