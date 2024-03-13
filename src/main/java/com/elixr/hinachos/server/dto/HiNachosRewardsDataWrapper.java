package com.elixr.hinachos.server.dto;

import com.elixr.hinachos.server.service.HiNachosService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
public class HiNachosRewardsDataWrapper {
    private String userName;
    private HiNachosService.RewardCounts totalRewardsCount;
    private List<HiNachosRewardsDataItem> heyNachosRewardsDataItemList;
}
