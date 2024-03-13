package com.elixr.hinachos.server.response;

import com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
public class HiNachosTagsResponse extends HiNachosServerBaseResponse {

    private Map<String, List<HiNachosRewardsSummaryDataItem>> hiNachosTagDetailsMap;
}
