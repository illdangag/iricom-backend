package com.illdangag.iricom.server.data.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PostBanInfoSearch extends SearchRequest {
    @Builder.Default
    private String reason = "";
}
