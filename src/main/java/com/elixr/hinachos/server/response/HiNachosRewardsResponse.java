package com.elixr.hinachos.server.response;

import com.elixr.hinachos.server.dto.HiNachosRewardsDataWrapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Builder
public class HiNachosRewardsResponse extends HiNachosServerBaseResponse {
    private HiNachosRewardsDataWrapper hinachosRewardsDataWrapper;
}
