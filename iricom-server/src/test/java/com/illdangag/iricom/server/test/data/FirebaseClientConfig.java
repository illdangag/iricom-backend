package com.illdangag.iricom.server.test.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FirebaseClientConfig {
    @JsonProperty("host")
    private String host;

    @JsonProperty("apiKey")
    private String apiKey;
}
