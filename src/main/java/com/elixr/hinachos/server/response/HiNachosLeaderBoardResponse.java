package com.elixr.hinachos.server.response;

import com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class HiNachosLeaderBoardResponse extends HiNachosServerBaseResponse{
    private List<HiNachosRewardsSummaryDataItem> hiNachosRewardsDataItemList;
}
