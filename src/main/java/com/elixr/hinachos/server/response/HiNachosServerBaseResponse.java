package com.elixr.hinachos.server.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class HiNachosServerBaseResponse {
   private int statusCode;
   private boolean success;
//   private String timeWhenMessageReceived;
   private String message;
}
