package com.elixr.hinachos.server.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HiNachosCustomLabelsResponse extends HiNachosServerBaseResponse {
    private List<HiNachosCustomLabelsResponseAttributes> hiNachosCustomLabelsResponseAttributesList;

    @Getter
    @Setter
    public static class HiNachosCustomLabelsResponseAttributes {
        private String domainName;
        private List<Map<String, Map<String, String>>> hiNachosCustomLabelsDataMapList;
    }
}
