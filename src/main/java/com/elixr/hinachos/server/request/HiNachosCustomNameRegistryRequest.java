package com.elixr.hinachos.server.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HiNachosCustomNameRegistryRequest {
    private String domainName;
    private Map<String, Map<String, String>> hiNachosCustomNameRegistryMap;

    @Getter
    @Setter
    public static class TabAttributes {
        private String sectionName;
        private String hiNachosDisplayName2;
        private String anotherKey21;
        private String anotherKey22;

    }
}
